import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RefineryUtilities;

import javax.swing.JLabel;

import java.awt.Font;
import java.util.LinkedList;

import javax.swing.JComboBox;
import javax.swing.JButton;


public class packetInfoWindow extends JFrame implements ActionListener{
	
	private JPanel summaryPanel;
	private JPanel perUserPanel;
	private JPanel settingsPanel;
	private JPanel panel_1;
	private JPanel panel_2;
	private JPanel panel_3;
	
	private int selectedUser;
	
	private boolean hasViewBeenPressed;
	
	/** The time series1 data. */
    private TimeSeries series1;
    private TimeSeries series1_2;
    private TimeSeries series2;	//this is for each individual user.
    private TimeSeries series3; //this is for each individual user.
    
    private LinkedList<TimeSeries> tcp_series;
    private LinkedList<TimeSeries> udp_series;
    
    /** The most recent value added. */
    private double lastValue1 = 100.0;
    private double lastValue1_2 = 100.0;
    private double lastValue3 = 100.0;
    private double lastValue4 = 100.0;
    private double lastValue5 = 100.0;
    
    private int playerSize;
    
    /** Timer to refresh graph after every 1/4th of a second */
    private Timer timer = new Timer(250, this);
	
	public packetInfoWindow() {
		playerSize = packetSniffer.getPlayerSize();
		System.out.println(playerSize);
		udp_series = new LinkedList<TimeSeries>();
		tcp_series = new LinkedList<TimeSeries>();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Packet Information");
		setSize(new Dimension(600,600));
		getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		panel.setLayout(new GridLayout(1, 0, 0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panel.add(tabbedPane);
		
		summaryPanel = new JPanel();
		perUserPanel = new JPanel();
		settingsPanel = new JPanel();
		
		tabbedPane.add("Summary",summaryPanel);
		summaryPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		panel_1 = new JPanel();
		summaryPanel.add(panel_1);
		panel_1.setLayout(null);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBounds(0, 0, 579, 177);
		panel_1.add(panel_4);
		
		this.series1 = new TimeSeries("Download Data");
		this.series1_2 = new TimeSeries("Upload Data");
		
        TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(this.series1);
		dataset.addSeries(this.series1_2);
		
		final JFreeChart chart = createChart(dataset,"TCP + UDP Upload and Download");
		
        chart.setBackgroundPaint(Color.LIGHT_GRAY);  					//Sets background color of chart
        final JPanel content = new JPanel(new BorderLayout());			//Created JPanel to show graph on screen
        final ChartPanel chartPanel = new ChartPanel(chart);			//Created Chartpanel for chart area
        panel_4.add(chartPanel);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 273));  //Sets the size of whole window (JPanel)
        
        /*
		panel_2 = new JPanel();
		summaryPanel.add(panel_2);
		panel_2.setLayout(null);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBounds(0, 0, 579, 177);
		panel_2.add(panel_5);
		
		TimeSeriesCollection dataset2 = new TimeSeriesCollection();
		for(int i=0;i<playerSize;i++){
			udp_series.add(new TimeSeries("user" + i));
			dataset2.addSeries(udp_series.peekLast());
		}
		
		final JFreeChart chart2 = createChart(dataset2,"UDP For All Users");
		
        chart2.setBackgroundPaint(Color.LIGHT_GRAY);  					//Sets background color of chart
        final JPanel content2 = new JPanel(new BorderLayout());			//Created JPanel to show graph on screen
        final ChartPanel chartPanel2 = new ChartPanel(chart2);			//Created Chartpanel for chart area
        panel_5.add(chartPanel2);
        chartPanel2.setPreferredSize(new java.awt.Dimension(600, 248));  //Sets the size of whole window (JPanel)
		
		/****
		 * 
		 * Insert graph 2 here
		 
		panel_3 = new JPanel();
		summaryPanel.add(panel_3);
		panel_3.setLayout(null);
		
		JPanel panel_6 = new JPanel();
		panel_6.setBounds(0, 0, 579, 177);
		panel_3.add(panel_6);
		
		TimeSeriesCollection dataset3 = new TimeSeriesCollection();
		for(int i=0;i<playerSize;i++){
			tcp_series.add(new TimeSeries("user" + i));
			dataset3.addSeries(tcp_series.peekLast());
		}
		
		final JFreeChart chart3 = createChart(dataset3,"TCP For All Users");
		
        chart3.setBackgroundPaint(Color.LIGHT_GRAY);  					//Sets background color of chart
        final JPanel content3 = new JPanel(new BorderLayout());			//Created JPanel to show graph on screen
        final ChartPanel chartPanel3 = new ChartPanel(chart3);			//Created Chartpanel for chart area
        panel_6.add(chartPanel3);
        chartPanel3.setPreferredSize(new java.awt.Dimension(600, 248));  //Sets the size of whole window (JPanel)
		
		/*
		 * 
		 * Insert graph 3 here.
		 * *
        
		tabbedPane.add("Per User",perUserPanel);
		perUserPanel.setLayout(null);
		String[] ipArray =  packetSniffer.getIpArray();
		//System.out.println(ipArray[0]);
		final JComboBox comboBox = new JComboBox(ipArray);
		comboBox.setBounds(10, 11, 460, 20);
		perUserPanel.add(comboBox);
		
		final JPanel panel_7 = new JPanel();
		panel_7.setBounds(10, 53, 559, 469);
		perUserPanel.add(panel_7);
		
		JButton btnView = new JButton("View");
		btnView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panel_7.removeAll(); //refreshes panel_7
				hasViewBeenPressed = true;
				selectedUser = comboBox.getSelectedIndex();
				series2 = new TimeSeries("Download Data");
				series3 = new TimeSeries("Upload Data");
				
		        TimeSeriesCollection dataset3 = new TimeSeriesCollection();
				dataset3.addSeries(series2);
				dataset3.addSeries(series3);
				
				final JFreeChart chart4 = createChart(dataset3,"TCP + UDP Upload and Download Per User");
				
		        chart4.setBackgroundPaint(Color.LIGHT_GRAY);  					//Sets background color of chart
		        final JPanel content4 = new JPanel(new BorderLayout());			//Created JPanel to show graph on screen
		        final ChartPanel chartPanel4 = new ChartPanel(chart4);			//Created Chartpanel for chart area
		        panel_7.add(chartPanel4);
		        chartPanel4.setPreferredSize(new java.awt.Dimension(600, 400));  //Sets the size of whole window (JPanel)
			}
		});
		btnView.setBounds(480, 10, 89, 23);
		perUserPanel.add(btnView);
		tabbedPane.add("Settings",settingsPanel);
		*/
		timer.setInitialDelay(1000);									//this timer jumpstarts all the graphs shown
		timer.start();
	}
	
	
	 /**
     * Creates a sample chart.
     *
     * @param dataset  the dataset.
     *
     * @return A sample chart.
     */
    private JFreeChart createChart(final XYDataset dataset,String title) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
            title,
            "Time",
            "Size",
            dataset,
            true,
            true,
            false
        );

        final XYPlot plot = result.getXYPlot();

        plot.setBackgroundPaint(new Color(0xffffe0));
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.lightGray);

        ValueAxis xaxis = plot.getDomainAxis();
        xaxis.setAutoRange(true);

        //Domain axis would show data of 60 seconds for a time
        xaxis.setFixedAutoRange(60000.0);  // 60 seconds
        xaxis.setVerticalTickLabels(true);

        ValueAxis yaxis = plot.getRangeAxis();
        yaxis.setRange(0.0, 1000.0);

        return result;
    }
    /**
     * Generates an random entry for a particular call made by time for every 1/4th of a second.
     *
     * @param e  the action event.
     */
    public void actionPerformed(final ActionEvent e) {

        this.lastValue1 = packetSniffer.getLastValue(1);
        this.lastValue1_2 = packetSniffer.getLastValue(2);
        
        final Millisecond now = new Millisecond();
        this.series1.add(now, this.lastValue1);
        this.series1_2.add(now, this.lastValue1_2);
        /*
        //add some code here that looks like the code above but is dynamic because of the uncertainty of the number of users.
        for(int i=0;i<playerSize;i++){
        	this.lastValue3 = packetSniffer.getPacketSizeOfIndex(i,"udp");//mode is udp
        	this.udp_series.get(i).add(now, lastValue3);
        	this.lastValue3 = packetSniffer.getPacketSizeOfIndex(i,"tcp");//mode is tcp
        	this.tcp_series.get(i).add(now, lastValue3);
        }
        if(hasViewBeenPressed){
        	this.lastValue4 = packetSniffer.getPacketSizeOfIndex(selectedUser,"udp") + packetSniffer.getPacketSizeOfIndex(selectedUser, "tcp");
        	this.series2.add(now,this.lastValue4);
        	this.lastValue4 = packetSniffer.getPacketSizeOfIndex(selectedUser,"udp2") + packetSniffer.getPacketSizeOfIndex(selectedUser, "tcp2");
        	this.series3.add(now,this.lastValue4);
        }*/
    }
}
