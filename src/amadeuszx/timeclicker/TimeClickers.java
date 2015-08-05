package amadeuszx.timeclicker;
import java.awt.EventQueue;

public class TimeClickers
{

	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				new Grafika();

			}
		});
	}

}
