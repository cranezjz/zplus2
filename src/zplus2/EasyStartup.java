package zplus2;


import org.eclipse.ui.IStartup;

public class EasyStartup implements IStartup {

	@Override
	public void earlyStartup() {
		MyConsole.printToConsole("EasyStartup earlyStartup()..");
	}
}
