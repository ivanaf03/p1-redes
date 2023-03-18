package es.udc.redes.webserver;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class Http {
    private String[] mensaje = null;
    private OutputStream out = null;
    private File file;
    private int state=200;

    public Http(String[] mensaje, OutputStream out, BufferedReader in){
        try {
            if (mensaje.length < 3 || !mensaje[2].startsWith("HTTP/") || !((mensaje[0].equals("GET") || mensaje[0].equals("HEAD")))) {
                this.mensaje = null;
            } else {
                this.mensaje = mensaje;
            }
            this.out = out;
            this.file = new File("p1-files" + mensaje[1]);
            if (this.mensaje == null) {
                state = 400;
            } else {
                if (!file.exists()) {
                    state = 404;
                }
                String a;
                String[] b;
                while(!"".equals(a= in.readLine())){
                    if(a.startsWith("If-Modified-Since:")){
                        b=a.split(": ");
                        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
                        Date fechaUltimaMod = new Date(file.lastModified());
                        Date modifiedSince = formatter.parse(b[1]);
                        if(!fechaUltimaMod.before(modifiedSince)){
                            state=304;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void print() throws IOException {
        switch (state) {
            case 200 -> out.write(("HTTP/1.0 200 OK\n").getBytes());
            case 304 -> out.write(("HTTP/1.0 304 NOT MODIFIED\n").getBytes());
            case 400 -> out.write(("HTTP/1.0 400 BAD REQUEST\n").getBytes());
            case 404 -> out.write(("HTTP/1.0 404 NOT FOUND\n").getBytes());
        }
            LocalDateTime fechaHoraActual = LocalDateTime.now();
            Date date = Date.from(fechaHoraActual.atZone(ZoneId.of("Europe/Madrid")).toInstant());
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
            out.write(("Date: " + formatter.format(date)+"\n").getBytes());
            out.write(("Server: Server de Ivan"+"\n").getBytes());
            if(!mensaje[0].equals("GET") && state==400 || state==304) return;
            formatter.applyPattern("EEE, dd MMM yyyy HH:mm:ss z");
            out.write(("Last-Modified: " + formatter.format(file.lastModified())+"\n").getBytes());
            out.write(("Content-Length: " + file.length()+"\n").getBytes());
            String tipo;
            if(state==400 || state==404){
                out.write("Content-Type: text/html".getBytes());
            } else {
            tipo = mensaje[1].substring(mensaje[1].lastIndexOf('.') + 1);
            switch (tipo) {
                case "txt", "plain" -> out.write("Content-Type: text/plain\n".getBytes());
                case "html" -> out.write("Content-Type: text/html\n".getBytes());
                case "ico", "png", "jpg", "gif" -> out.write(("Content-Type: image/" + tipo+"\n").getBytes());
                case "pdf", "docx" -> out.write(("Content-Type: document/" + tipo+"\n").getBytes());
                default -> out.write("Content-Type: application/octet-stream\n".getBytes());
            }
        }
        out.write("\r\n".getBytes());
        if(mensaje[0].equals("GET")) {
            if (state == 400) {
                file = new File("p1-files/error400.html");
            }
            if (state == 404) {
                file = new File("p1-files/error404.html");
            }
            byte[] content = Files.readAllBytes(file.toPath());
            out.write(content);
        }
        out.flush();
        out.close();
    }
}
