package main;

import java.io.File;

import javafx.concurrent.Task;

public class DecryptTask extends Task<Void>  {
	
	private File file;
	
	public DecryptTask(File file) {
		this.file = file;
	}

	@Override
	protected Void call() throws Exception {
		
		return null;
	}

}
