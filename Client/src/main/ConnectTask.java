package main;

import java.net.Socket;

import javafx.concurrent.Task;

public class ConnectTask extends Task<Void>{

	@Override
	protected Void call() throws Exception {
		
		//u mnie na VM: 10.0.2.2
		//na windowsie zmienić na localhost
		try(Socket socket = new Socket("10.0.2.2", 1234)){
			
			socket.close();
		}
		return null;
	}
	
}
