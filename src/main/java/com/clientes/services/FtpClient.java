package com.clientes.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpClient implements IFtpClient{
	
private static Logger log = LoggerFactory.getLogger(FtpClient.class);
    
	private final String FTP_DOMAIN = "172.27.80.188";
	private final int FTP_PORT = 21;
	private final String FTP_USER = "";
	private final String FTP_PASSWORD = "";
	private FTPClient ftp;
	
	private String FTP_FILE = "";

	@Override
	public String StartProcess() {
		try {
			this.Open();
			
			//Valida que exista el archivo
			Collection<String> file = this.ListFiles("/");
		    log.info("... Resultado de la busqueda: "+file.size());
		    
		    if(file.size() == 1) {
		    	
		    	//Creamos la carpeta si no existe
				File folderValidate = new File("download"+File.separator);
				if(!folderValidate.exists()) {
					log.info("... La carpeta download no existe y se procede a crearla");
					folderValidate.mkdir();
				}
		    	
				//Buscamos el archivo
		    	file.forEach((e)->{
		    		log.info("... Se encontró el fichero "+e);
		    		FTP_FILE = e;
		    	});
		    	
				this.DownloadFile(FTP_FILE);
				
				log.info("... Finalizando configuracion FTP");
			    this.Close();
				
				return FTP_FILE;
		    	
		    }else {
		    	log.info("... El archivo no se encontró o la carpeta contiene 2 ficheros o más");
		    }
		    
		    
		} catch (IOException e) {
			log.info("Error al procesar fichero por FTP "+e.getLocalizedMessage());
		}
		
		return null;
	}
	
	@Override
	public void DownloadFile(String ftpFile) {
		try {
			
			//Descargando archivo
			if(!ftpFile.isEmpty()) {
				log.info("... Iniciando proceso de descarga del fichero "+ftpFile);
				
				FileOutputStream out = new FileOutputStream("download/"+ftpFile);
				
				if(ftp.retrieveFile("/"+ftpFile, out) == true) {
					log.info("... Fichero descargado");
				}else {
					log.info("... No se pudo descargar fichero");
				}
				
				out.close();
				
			}
			
		} catch (IOException e) {
			log.info("Error al descargar fichero por FTP "+e.getLocalizedMessage());
		}
	}
	
	@Override
    public void Open() throws IOException {
    	log.info("... Iniciando configuracion FTP");
       
    	ftp = new FTPClient();
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        
        log.info("... Accediendo al servicio "+FTP_DOMAIN+" en el puerto "+FTP_PORT);
        ftp.connect(FTP_DOMAIN, FTP_PORT);
        
        int reply = ftp.getReplyCode();
        log.info("... Status de conexion "+reply);
        
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }

        if(ftp.login(FTP_USER, FTP_PASSWORD) == true) {
        	log.info("... Credenciales de conexion correctas ");
        }else {
        	log.info("... Credenciales de conexion incorrectas ");
        	ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }
    }

	@Override
    public void Close() throws IOException {
        ftp.disconnect();
    }
    
	@Override
    public Collection<String> ListFiles(String path) throws IOException {
        FTPFile[] files = ftp.listFiles(path);
        return Arrays.stream(files)
          .map(FTPFile::getName)
          .collect(Collectors.toList());
    }

}
