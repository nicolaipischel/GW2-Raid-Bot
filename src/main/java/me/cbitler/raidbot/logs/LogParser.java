package me.cbitler.raidbot.logs;

import me.cbitler.raidbot.utility.EnvVariables;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Upload log files sent to the bot to a local parser, dps.report, and GW2 Raidar
 * For much of this it calls out to the commandline so it tries to sanitize the input first
 * @author Christopher Bitler
 */
public class LogParser implements Runnable {
    private PrivateChannel channel;
    private Message.Attachment attachment;
    private HashMap<String, String> invalidCharacters = new HashMap<>();

    /**
     * Create a new instance of the log parser
     * @param channel The channel the file was sent in
     * @param attachment The file that was sent
     */
    public LogParser(PrivateChannel channel, Message.Attachment attachment) {
        this.channel = channel;
        this.attachment = attachment;
        this.populateHashMap();
    }

    /**
     * Run the upload/parsing process
     */
    @Override
    public void run() {
        channel.sendMessage("EVTC file erhalten... Download gestartet").queue();
        String fileName = attachment.getFileName();
        for(Map.Entry<String,String> invalidCharacter : invalidCharacters.entrySet()) {
            fileName = fileName.replace(invalidCharacter.getKey(), invalidCharacter.getValue());
        }
        if(fileName.contains("..")) {
            channel.sendMessage("Ungültige Zeichen im Dateinamen gefunden! '..'").queue();
            return;
        }
        File file = new File("parser/" + attachment.getFileName());

        if(file.exists()) file.delete();
        attachment.download(file);
        channel.sendMessage("Datei heruntergeladen.. Umwandlung gestartet.").queue();

        String finalFileName = "";
        String dpsReportUrl = "";

        try {
            Process p = Runtime.getRuntime().exec("dotnet parser/GuildWars2EliteInsights.dll \"parser/" + attachment.getFileName() + "\" \"/var/www/html/logs/\"");
            System.out.println("dotnet parser/GuildWars2EliteInsights.dll \"parser/" + attachment.getFileName() + "\" \"/var/www/html/logs/\"");
            String line;
            BufferedReader bri = new BufferedReader
                    (new InputStreamReader(p.getInputStream()));
            BufferedReader bre = new BufferedReader
                    (new InputStreamReader(p.getErrorStream()));
            while ((line = bri.readLine()) != null) {
                if(line.contains(".html")) {
                    finalFileName = line;
                }
            }
            bri.close();
            while ((line = bre.readLine()) != null) {
                if(line.contains(".html")) {
                    finalFileName = line;
                }
            }
            bre.close();

            p.waitFor();

            channel.sendMessage("Datei umgewandelt. HTML generiert.").queue();

            channel.sendMessage("Datei zu dps.report hochgeladen").queue();

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost fileUpload = new HttpPost("https://dps.report/uploadContent?json=1");
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file",  file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
            HttpEntity multipart = builder.build();
            fileUpload.setEntity(multipart);
            CloseableHttpResponse response = client.execute(fileUpload);
            HttpEntity responseEntity = response.getEntity();
            String responseText = IOUtils.toString(responseEntity.getContent());

            JSONParser parser = new JSONParser();

            try {
                JSONObject dpsReportResponse = (JSONObject) parser.parse(responseText);
                dpsReportUrl = (String) dpsReportResponse.get("permalink");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            EnvVariables variables = new EnvVariables();
            variables.loadFromEnvFile();
            channel.sendMessage("dps.report fertig. Upload zu gw2raidar gestartet").queue();
            String tokenResponse =
                    this.handleCurl(new String[] {"curl", "-s", "-F", "username=" + variables.getValue("RAIDAR_USERNAME"), "-F", "password=" + variables.getValue("RAIDAR_PASSWORD"), "https://www.gw2raidar.com/api/v2/token"});
            System.out.println(tokenResponse);
            JSONObject token =
                    (JSONObject) parser.parse(tokenResponse);
            String tokenString = (String) token.get("token");
            String upload =
                    this.handleCurl(new String[] {"curl", "-s", "-X", "PUT", "-H", "Authorization: Token " + tokenString, "-F", "file=@parser/" + attachment.getFileName(), "https://www.gw2raidar.com/api/v2/encounters/new"});
            JSONObject uploadResult = (JSONObject) parser.parse(upload);
            boolean raidarUploadSuccess = uploadResult.get("detail") == null;

            channel.sendMessage("Local parser: http://logs.v-l.pw/logs/" + finalFileName + "\n ").queue();
            channel.sendMessage("dps.report: " + dpsReportUrl.replace("\\","")).queue();
            if(raidarUploadSuccess) {
                channel.sendMessage("Report zu GW2Raidar hochgeladen(keine Url übermittelt)").queue();
            }
        } catch (IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void populateHashMap() {
        invalidCharacters.put(":", "_");
        invalidCharacters.put(";", "_");
        invalidCharacters.put("?", "_");
        invalidCharacters.put("..", ".");
    }

    private String handleCurl(String[] processStrings) throws IOException, InterruptedException {
        String data = "";
        ProcessBuilder pb = new ProcessBuilder(processStrings);
        Process p = pb.start();
        String line;
        BufferedReader bri = new BufferedReader
                (new InputStreamReader(p.getInputStream()));
        BufferedReader bre = new BufferedReader
                (new InputStreamReader(p.getErrorStream()));
        while ((line = bri.readLine()) != null) {
            data += line;
        }
        bri.close();
        while ((line = bre.readLine()) != null) {
            data += line;
        }
        bre.close();

        p.waitFor();

        return data;
    }
}
