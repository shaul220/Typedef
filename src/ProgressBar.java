
public class ProgressBar {
	
	private String _currentProcess;
	
	public ProgressBar(String currentProcess)
	{
		_currentProcess = currentProcess;
	}
	
	public ProgressBar()
	{
		_currentProcess = "";
	}
	
	public void setCurrentProcess(String currentProcess)
	{
		_currentProcess = currentProcess;
		System.out.print(_currentProcess + ":\n");
		showProgress(0);
	}
	
	public void showProgress(int precentage)
	{
		System.out.print(precentage + "%\r");
		if(precentage == 100)
			done();
	}
	
	public void done()
	{
		System.out.print("Done\n\n");
	}
	
}
