package amadeuszx.timeclicker;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Grafika extends JFrame implements ChangeListener
{
	class PreferncjeUzbrojeniaLisener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(e.getSource().equals(bronPreferencje[0]))
			celowanie.szukajUlubionych(0);
			if(e.getSource().equals(bronPreferencje[1]))
			celowanie.szukajUlubionych(1);
			if(e.getSource().equals(bronPreferencje[2]))
			celowanie.szukajUlubionych(2);
			if(e.getSource().equals(bronPreferencje[3]))
			celowanie.szukajUlubionych(3);
			if(e.getSource().equals(bronPreferencje[4]))
			celowanie.szukajUlubionych(4);
		}
		
	}
	class RobotLisener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			ustawianie1.setEnabled(false);
			ustawianie2.setEnabled(false);
			koniec = !koniec;
			start.setForeground(Color.RED);
			start.setText("STOP");

			int tmp;

			if (xp > xk)
			{
				tmp = xk;
				xk = xp;
				xp = tmp;
			}
			if (yp > yk)
			{
				tmp = yk;
				yk = yp;
				yp = tmp;
			}



			new Thread()
			{
				boolean celowanieB = false;
				@Override
				public void run()
				{
					while (true)
					{
						int tmpx = lokalizacjaKursora()[0];
						int tmpy = lokalizacjaKursora()[1];

						if (tmpx >= xp && tmpx <= xk && tmpy >= yp-2 && tmpy <= yk) //powiekszenie aktywnego obszaru do gory
						{
							robot.mousePress(InputEvent.BUTTON1_MASK);
							robot.mouseRelease(InputEvent.BUTTON1_MASK);
							robot.delay(opoznienieStrzalu);
							if(!celowanieB && autocelowanie.isSelected())
							{
								celowanieB = true;
								if(celowanie!=null){
									celowanie.stop = false;
									celowanie.szukajCelu();
								}
							}
						}
						else
						{
							celowanieB = false;
							if(celowanie!=null)celowanie.stop = true;
						}
						
						if (koniec)
						{
							break;
						}
					}
					
					umiejetnosc.setSelected(false);
					rekrut.setSelected(false);

					ustawianie1.setEnabled(true);
					ustawianie2.setEnabled(true);
					start.setForeground(Color.GREEN);
					start.setText("START");
					start.setEnabled(true);
				}
			}.start();

		}

	}

	class UstawienieKursoraListener implements AWTEventListener
	{
		@Override
		public void eventDispatched(AWTEvent event)
		{
			int[] tmpXY=lokalizacjaKursora();
			if (ustawianie1.isSelected())
			{
				xp = tmpXY[0];
				yp = tmpXY[1];
				xPole1.setText(tmpXY[0] + "");
				yPole1.setText(tmpXY[1] + "");
				ustawianie1.setSelected(false);
			}

			if (ustawianie2.isSelected())
			{
				xk = tmpXY[0];
				yk = tmpXY[1];
				xPole2.setText(tmpXY[0] + "");
				yPole2.setText(tmpXY[1] + "");
				ustawianie2.setSelected(false);
			}

		}

	}

	Robot robot;
	Cele celowanie;
	private Grafika gg;
	private JPanel wytycznePanel, zdolnosciPanel, rekrutacjaPanel;

	private int xp = 450, yp = 200, xk = 900, yk = 670;
	public static boolean koniec = true;
	private JButton start;

	private JCheckBox ustawianie1, ustawianie2, rekrut, umiejetnosc, autocelowanie;
	private JCheckBox[] bron = new JCheckBox[5];
	private JRadioButton[] bronPreferencje = new JRadioButton[5];
	private ButtonGroup grupa = new ButtonGroup();
	
	private JSlider suwakCzKliku, suwakCzRekrutacji, suwakCzUmiejetnosci;
	private JLabel etykietaCzKliku, etykietaCzRekrutacji, etykietaCzUmiejetnosci, bronPlakietka;

	private JTextField xPole1, yPole1, xPole2, yPole2;

	private int opoznienieStrzalu = 25;
	private int czestUmiejtn = 60;
	private int czestWerbunku = 30;

	public Grafika()
	{
		gg =this;
		setLocation(1100, 10);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setAlwaysOnTop(true);
		setLayout(null);
		setResizable(false);
		setTitle("AutoTimeClikers");
		Toolkit.getDefaultToolkit().addAWTEventListener(new UstawienieKursoraListener(), AWTEvent.FOCUS_EVENT_MASK);


		try
		{
			robot = new Robot();
		}
		catch (AWTException e)
		{
			e.printStackTrace();
		}
		celowanie = new Cele(gg);
		int yprzes =0, delta =0;
		


		wytycznePanel = wytyczneTworz();
		wytycznePanel.setBounds(0, yprzes, 160, delta=100);
		wytycznePanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.RED));	

		rekrutacjaPanel = rekrutacjeTworz();
		rekrutacjaPanel.setBounds(0, yprzes+=delta, 160, delta=60);
		rekrutacjaPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GREEN));

		zdolnosciPanel = zdolnosciTworz();
		zdolnosciPanel.setBounds(0, yprzes+=delta, 160, delta=40);
		zdolnosciPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLUE));
		
		start = new JButton();
		start.setForeground(Color.GREEN);
		start.setText("START");
		start.addActionListener(new RobotLisener());
		start.setBounds(0, yprzes+=delta, 160, delta=20);

		add(wytycznePanel);
		add(rekrutacjaPanel);
		add(zdolnosciPanel);
		add(start);

		timery();
		korekta();

		setMinimumSize(new Dimension(167, yprzes+=delta+30));
		pack();
		setVisible(true);
	}
	
	JPanel wytyczneTworz(){
		JPanel zz = new JPanel();
		zz.setLayout(null);
		
		ustawianie1 = new JCheckBox("Beginning");
		ustawianie1.setBounds(1, 1, 89, 19);
		ustawianie1.setToolTipText("Beginning of clicking area");
		xPole1 = new JTextField(xp+"", 4);
		xPole1.setHorizontalAlignment(SwingConstants.RIGHT);
		xPole1.setBounds(90, 1, 35, 19);
		xPole1.setToolTipText("x coordinate");
		xPole1.setEnabled(false);
		yPole1 = new JTextField(yp+"", 4);
		yPole1.setHorizontalAlignment(SwingConstants.RIGHT);
		yPole1.setBounds(125, 1, 34, 19);
		yPole1.setToolTipText("y coordinate");
		yPole1.setEnabled(false);
		
		ustawianie2 = new JCheckBox("End");
		ustawianie2.setBounds(1, 20, 89, 20);
		ustawianie2.setToolTipText("End of clicking area");
		xPole2 = new JTextField(xk+"", 4);
		xPole2.setHorizontalAlignment(SwingConstants.RIGHT);
		xPole2.setBounds(90, 20, 35, 20);
		xPole2.setToolTipText("x coordinate");
		xPole2.setEnabled(false);
		yPole2 = new JTextField(yk+"", 4);
		yPole2.setHorizontalAlignment(SwingConstants.RIGHT);
		yPole2.setBounds(125, 20, 34, 20);
		yPole2.setToolTipText("y coordinate");
		yPole2.setEnabled(false);
		
		suwakCzKliku = new JSlider(SwingConstants.HORIZONTAL, 10, 50, 25);
		suwakCzKliku.addChangeListener(this);
		suwakCzKliku.setBounds(1, 40, 119, 20);
		suwakCzKliku.setMajorTickSpacing(2);

		etykietaCzKliku = new JLabel(1000/opoznienieStrzalu+"");
		etykietaCzKliku.setToolTipText("Click per second");
		etykietaCzKliku.setBounds(125, 40, 40, 19);
		
		autocelowanie = new JCheckBox("Auto targeting");
		autocelowanie.setSelected(true);
		autocelowanie.setToolTipText("Auto targeting");
		autocelowanie.setBounds(1, 60, 120, 20);
		
		ActionListener bronListener = new PreferncjeUzbrojeniaLisener();
		for(int i=0; i<5; i++){
			bronPreferencje[i] = new JRadioButton();
			opisyBroni(i, bronPreferencje[i], false);
			bronPreferencje[i].setBounds(20*i+60, 80, 19, 19);
			bronPreferencje[i].addActionListener(bronListener);
			grupa.add(bronPreferencje[i]);
			zz.add(bronPreferencje[i]);
		}
		bronPreferencje[2].setSelected(true);
		
		zz.add(ustawianie1);
		zz.add(xPole1);
		zz.add(yPole1);
		zz.add(ustawianie2);
		zz.add(xPole2);
		zz.add(yPole2);
		zz.add(suwakCzKliku);
		zz.add(etykietaCzKliku);
		zz.add(autocelowanie);
		
		return zz;
	}
	
	void opisyBroni(int i, JToggleButton bron, boolean zaznaczenie)
	{
		switch(i)
		{
			case 0: bron.setToolTipText("Pulse Pistol"); break;
			case 1: bron.setToolTipText("Flak Cannon"); break;
			case 2: bron.setToolTipText("Spread Rifle"); break;
			case 3: bron.setToolTipText("Rocket Launcher"); break;
			case 4: bron.setToolTipText("Particle Beam"); break;
		}
		bron.setSelected(zaznaczenie); 
	}
	
	private JPanel rekrutacjeTworz(){
		JPanel zz = new JPanel();
		zz.setLayout(null);
		
		suwakCzRekrutacji = new JSlider(SwingConstants.HORIZONTAL, 15, 120, 30);
		suwakCzRekrutacji.addChangeListener(this);
		suwakCzRekrutacji.setBounds(1, 1, 119, 19);
		suwakCzRekrutacji.setMajorTickSpacing(15);
		suwakCzRekrutacji.setSnapToTicks(true);
		zz.add(suwakCzRekrutacji);
		
		etykietaCzRekrutacji = new JLabel(czestWerbunku+"");
		etykietaCzRekrutacji.setToolTipText("Hiring frequency");
		etykietaCzRekrutacji.setBounds(125, 1, 39, 19);
		zz.add(etykietaCzRekrutacji);
		
		bronPlakietka = new JLabel("Weapon");
		bronPlakietka.setBounds(1, 20, 59, 20);
		zz.add(bronPlakietka);
		
		for(int i=0; i<5; i++){
			bron[i] = new JCheckBox();
			opisyBroni(i, bron[i], true);
			bron[i].setBounds(20*i+60, 20, 19, 20);
			zz.add(bron[i]);
		}

		rekrut = new JCheckBox("Recruit");
		rekrut.setToolTipText("Recruitment selected");
		rekrut.setBounds(1, 40, 120, 19);
		zz.add(rekrut);
		
		return zz;
	}
	
	private JPanel zdolnosciTworz(){
		JPanel zz = new JPanel();
		zz.setLayout(null);
		
		suwakCzUmiejetnosci = new JSlider(SwingConstants.HORIZONTAL, 60, 600, 60);
		suwakCzUmiejetnosci.addChangeListener(this);
		suwakCzUmiejetnosci.setBounds(1, 1, 119, 19);
		suwakCzUmiejetnosci.setMajorTickSpacing(60);
		suwakCzUmiejetnosci.setSnapToTicks(true);
		
		etykietaCzUmiejetnosci  = new JLabel(czestUmiejtn+"");
		etykietaCzUmiejetnosci.setToolTipText("Frequency of using skills");
		etykietaCzUmiejetnosci.setBounds(125, 1, 39, 19);
		
		umiejetnosc = new JCheckBox("Skills");
		umiejetnosc.setToolTipText("Using skills");
		umiejetnosc.setBounds(1, 20, 118, 19);
	
		zz.add(suwakCzUmiejetnosci);
		zz.add(etykietaCzUmiejetnosci);
		zz.add(umiejetnosc);
		
		
		return zz;
	}
	
	void timery(){

		//werbunek
		new Thread(){
			int licznik=0;
			@Override
			public void run()
			{
				while(true){
					try
					{
						if(rekrut.isSelected() && (bron[0].isSelected() || bron[1].isSelected() || bron[2].isSelected() || bron[3].isSelected() || bron[4].isSelected()))
						{
								Thread.sleep(czestWerbunku*1000);
								licznik = werbuj(licznik);
						}
						else
						{
								Thread.sleep(60_000);
						}
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		}.start();
		
		//umiejetnosci
		new Thread(){
			@Override
			public void run()
			{
				while(true){
					try
					{
						if(umiejetnosc.isSelected())
						{
								Thread.sleep(czestUmiejtn*1000);
								skille();
						}
						else
						{
								Thread.sleep(60_000);
						}
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		}.start();
	}



	int[] lokalizacjaKursora()
	{
		int[] wsp = new int[2];
		wsp[0] = (int) MouseInfo.getPointerInfo().getLocation().getX();
		wsp[1] = (int) MouseInfo.getPointerInfo().getLocation().getY();
		return wsp;
	}



	@Override
	public void stateChanged(ChangeEvent e)
	{
		JSlider zr = (JSlider) e.getSource();
		if(zr.equals(suwakCzKliku))
		{
			suwakCzKliku.setToolTipText("Delay " + zr.getValue()+"ms");
			etykietaCzKliku.setText("" + Math.round(1000 / zr.getValue()));
			opoznienieStrzalu = zr.getValue();
		}
		else if(zr.equals(suwakCzRekrutacji))
		{
			suwakCzRekrutacji.setToolTipText("Hiring at " + zr.getValue());
			czestWerbunku = zr.getValue();
			etykietaCzRekrutacji.setText("" + zr.getValue());
		}
		else if(zr.equals(suwakCzUmiejetnosci))
		{
			suwakCzUmiejetnosci.setToolTipText("Skills at " + zr.getValue());
			czestUmiejtn = zr.getValue();
			etykietaCzUmiejetnosci.setText("" + zr.getValue());
		}
	}

	private int werbuj(int licz){
		char[] klawisze = {65, 83, 68, 70, 71};	


		boolean znalezione = false;
		while(!znalezione){
			if( bron[licz].isSelected())
			{
				robot.keyPress((char)klawisze[licz]);
				robot.keyRelease((char)klawisze[licz]);
				licz++;
				znalezione = true;
			}
			else
				licz++;
			
			if(licz>=5) licz=0;
		}

		return licz;
	}
	private void skille(){
		char[] klawisze = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
		for(char xx : klawisze)
		{
			robot.keyPress(xx);
			robot.keyRelease(xx);
		}
	}	
	
	private void korekta(){
		new Thread(){
			@Override
			public void run()
			{
				while(true){
					try
					{
						Thread.sleep(120_000);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}

					robot.mouseMove(xp+(xk-xp)/2, yp+(yk-yp)/2);
				}
			}
		}.start();
	}
	
	int getXp()
	{
		return xp;
	}

	int getYp()
	{
		return yp;
	}

	int getXk()
	{
		return xk;
	}

	int getYk()
	{
		return yk;
	}
	
	
	
	
}
