package amadeuszx.timeclicker;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

public class Cele
{
	
	private int wzorSzRNiebieski = new Color(0, 104, 255).getRGB();
	private int wzorSzRZolty = new Color(247, 14, 0).getRGB();
	private int wzorSzRCzerwony = new Color(255, 0, 0).getRGB();
	private int wzorSzRZielony = new Color(0, 255, 40).getRGB();
	private int wzorSzRFioletowy = new Color(143, 0, 255).getRGB();
	private int wzorSzRCzarny = new Color(0, 0, 0).getRGB();
	
	private int wzorSzZolty = new Color(254, 255, 0).getRGB();
	private int wzorSzBialy = new Color(255, 255, 255).getRGB();
	
	private final int[] pakietKolorow = {wzorSzZolty, wzorSzBialy};
	
	int ulubionaRamka = wzorSzRCzerwony;
	int[] pakietRamek = {wzorSzRNiebieski, wzorSzRFioletowy, wzorSzRZielony, wzorSzRZolty};
	
	private Color[] wzorTecza1 = {new Color(0, 254, 247), new Color(0, 254, 247), new Color(0, 254, 246), new Color(0, 254, 247)};
	private int[] wzorSzTecza1 = new int[wzorTecza1.length];
	private Color[] wzorTecza2 = {new Color(255, 126, 0), new Color(255, 121, 0), new Color(255, 115, 0), new Color(255, 109, 0)};
	private int[] wzorSzTecza2 = new int[wzorTecza1.length];
	private Color[] wzorTecza3 = {new Color(12, 255, 0), new Color(12, 255, 0), new Color(12, 255, 0), new Color(12, 255, 0)};
	private int[] wzorSzTecza3 = new int[wzorTecza1.length];
	private int[] wzorSzTime = new int[wzorTecza1.length];
	private int[] wzorSzCzerwony = new int[wzorTecza1.length];
	
	private Grafika gg;
	boolean stop = false;
	int[][] result;
	
	private int xp, yp;

	// x 600 - 1200 y 400 - 600
	public Cele(Grafika ggt)
	{
		gg = ggt;

		for (int i = 0; i < wzorTecza1.length; i++)
		{
			wzorSzTecza1[i] = wzorTecza1[i].getRGB();
			wzorSzTecza2[i] = wzorTecza2[i].getRGB();
			wzorSzTecza3[i] = wzorTecza3[i].getRGB();
			wzorSzTime[i] = new Color(50, 111, 201).getRGB();
			wzorSzCzerwony[i] =  new Color(255, 0, 0).getRGB();
		}

	}

	void szukajCelu()
	{
		new Thread()
		{
			@Override
			public void run()
			{
			
				while (!stop)
				{
					//pobieranie obrazka do przeszukania
					BufferedImage bi = gg.robot.createScreenCapture(obszarSzukania());
				
					int width = bi.getWidth();
					int height = bi.getHeight();
					result = new int[height][width];
					//przetworzenie obrazka na int - ¿eby szybko szukaæ
					for (int row = 0; row < height; row++)
						for (int col = 0; col < width; col++)
							result[row][col] = bi.getRGB(col, row);
					
					//hierarchia ostrza³u klocków
					testujTecza(wzorSzTecza1);
					testujTecza(wzorSzTecza2);
					testujTecza(wzorSzTecza3);
					try
					{
						int tmp=0;
						
						if((tmp=namierzanieMulti(ulubionaRamka)) != 0)
							Thread.sleep(tmp * 100);
							else 
								if((tmp=namierzanieMulti(pakietRamek)) != 0)
								Thread.sleep(tmp * 150);
								else 
									if(namierzanie(wzorSzCzerwony))
										Thread.sleep(100);
										else
											if(namierzanie(wzorSzTime))
												Thread.sleep(800);
												else
													if((tmp=namierzanieMulti(wzorSzRCzarny)) != 0)
													Thread.sleep(tmp * 250);
						
					}
					
					catch (InterruptedException e)
					{
							e.printStackTrace();
					}
						
				}// while loop
			
			}

		}.start();

	}
	
	void testujTecza(final int[] koloryTeczy){
		new Thread(){
			@Override
			public void run()
			{
				if(namierzanie(koloryTeczy))
					{
					gg.robot.mousePress(InputEvent.BUTTON1_MASK);
					gg.robot.mouseRelease(InputEvent.BUTTON1_MASK);
					};
			}
		}.start();
	}
	
	private boolean namierzanie(int[] wzor){
		boolean kolorek = false;
		int[][] tab = result;
		
		DOROBOTY:
			for (int yy = tab.length-1; yy >= 0; yy--)
				for (int xx = 0; xx < tab[yy].length; xx++)
					if (tab[yy][xx] == wzor[0]) //jeœli na szukanym obrazku znaleŸliœmy punkt w szukanym kolorze 
						for (int xPodpunkt = xx + 1; xPodpunkt < xx + wzor.length; xPodpunkt++)  //szukamy kolejnych 9 punktów
						{
							if (xPodpunkt<(gg.getXk()-gg.getXp()) && tab[yy][xPodpunkt] == wzor[xPodpunkt - xx]) //sprawdzamy kolejne pkt.
							{
								if (xPodpunkt == xx + wzor.length - 1) //jeœli doszliœmy do ostatniego pkt.
								{
									if(stop)break DOROBOTY; // wy³¹czenie automatycznego strzelania
									
									gg.robot.mouseMove(xp + xx, yp + yy);
									kolorek = true;
									break DOROBOTY;
								}
							}
							else
								break;
					
						}

		return kolorek;
	}
	
	private int namierzanieMulti(int... ramka){
		int kolorek = 0;
		int[][] tab = result;

//		long start = System.currentTimeMillis();
		DOROBOTY:
			for (int yy = tab.length-1; yy >= 0; yy--)
				for (int xx = 0; xx < tab[yy].length; xx++)
					if (porownanie(tab[yy][xx], ramka)) //jeœli na szukanym obrazku znaleŸliœmy punkt w szukanym kolorze 
						for (int xPodpunkt = xx + 1; xPodpunkt < xx + 9; xPodpunkt++)  //szukamy kolejnych x punktów
						{
							if (xPodpunkt<(gg.getXk()-gg.getXp()) && (tab[yy][xPodpunkt] == pakietKolorow[0] || tab[yy][xPodpunkt] == pakietKolorow[1])) //sprawdzamy kolejne pkt.
							{
								if (xPodpunkt == xx + 7) //jeœli doszliœmy do ostatniego pkt.
								{
									if(stop)break DOROBOTY; // wy³¹czenie automatycznego strzelania
									
									gg.robot.mouseMove(xp + xx, yp + yy);
									if (tab[yy][xPodpunkt] == pakietKolorow[0])
									kolorek = 4;
									if (tab[yy][xPodpunkt] == pakietKolorow[1])
									kolorek = 2;

									break DOROBOTY;
								}
							}
							else
								break;
						
						}
		
//		System.out.println(System.currentTimeMillis()-start);
		return kolorek;
	}
	private boolean porownanie(int tab, int... ramka){
		if(ramka.length > 1)
			return tab == ramka[0] || tab == ramka[1] || tab == ramka[2] || tab == ramka[3];
		else
			return tab == ramka[0];
	}	

	public void szukajUlubionych(int x)
	{
		switch(x)
		{
		case 0: 		
			ulubionaRamka = wzorSzRNiebieski;
			int[] tmp0 = {wzorSzRCzerwony, wzorSzRFioletowy, wzorSzRZielony, wzorSzRZolty};
			pakietRamek = tmp0;
			break;
		case 1: 		
			ulubionaRamka = wzorSzRZolty;
			int[] tmp1 = {wzorSzRCzerwony, wzorSzRNiebieski, wzorSzRFioletowy, wzorSzRZielony};
			pakietRamek = tmp1;
			break;
		case 2: 		
			ulubionaRamka = wzorSzRCzerwony;
			int[] tmp2 = {wzorSzRNiebieski, wzorSzRFioletowy, wzorSzRZielony, wzorSzRZolty};
			pakietRamek = tmp2;
			break;
		case 3: 		
			ulubionaRamka = wzorSzRZielony;
			int[] tmp3 = {wzorSzRCzerwony, wzorSzRNiebieski, wzorSzRFioletowy, wzorSzRZolty};
			pakietRamek = tmp3;
			break;
		case 4: 		
			ulubionaRamka = wzorSzRFioletowy;
			int[] tmp4 = {wzorSzRCzerwony, wzorSzRNiebieski, wzorSzRZielony, wzorSzRZolty};
			pakietRamek = tmp4;
			break;
		default: 		
			ulubionaRamka = wzorSzRCzerwony;
			int[] tmp = {wzorSzRNiebieski, wzorSzRFioletowy, wzorSzRZielony, wzorSzRZolty};
			pakietRamek = tmp;
			break;
		}
		
	}
	
	
	private Rectangle obszarSzukania(){
		xp = gg.getXp();
		int xwym = gg.getXk()-xp;
		yp = gg.getYp();
		int ywym = gg.getYk()-yp;
		
		return new Rectangle(xp, yp, xwym, ywym);
	}
}