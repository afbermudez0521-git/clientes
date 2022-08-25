package com.clientes.services;

import java.io.IOException;
import java.util.Collection;

public interface IFtpClient {

	public void Open() throws IOException;

	public void Close() throws IOException;

	public Collection<String> ListFiles(String path) throws IOException;

	public void DownloadFile(String ftpFile);

	public String StartProcess();
	
}
