
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class GUI
{
    //Nametag for Containers
    private final JFrame frame; private final JPanel mainPanel; private CustomPanel chartPanel;
    private JScrollPane tablePane; private JScrollPane chartPane; private JTable table;
    private JButton addBtn; private JButton removeBtn; private JButton computeBtn; private JLabel wtLabel;
    private JLabel wtResultLabel; private JLabel tatLabel; private JLabel tatResultLabel;
    private JComboBox option; private DefaultTableModel model; private final CustomPanel utilPanel;
    private final JScrollPane utilPane; private JLabel tableOption; private JLabel addrmvLabel;
    
    private JLabel chartLabel;
    private JLabel timelineLabel;
    
    public GUI()
    {
        model = new DefaultTableModel(new String[]{"Process", "AT", "BT", "Priority", "WT", "TAT"}, 0);
        
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setPreferredSize(new Dimension(250, 1000));
        tablePane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        tablePane.setBounds(25, 125, 450, 250);
        
        addBtn = new JButton("Add");
        addBtn.setBounds(180, 53, 95, 25);
        addBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        addBtn.addActionListener((ActionEvent e) -> {
            model.addRow(new String[]{"", "", "", "", "", ""});
        });
        
        removeBtn = new JButton("Remove");
        removeBtn.setBounds(290, 53, 95, 25);
        removeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        removeBtn.addActionListener((ActionEvent e) -> {
            int row = table.getSelectedRow();
            
            if (row > -1) {
                model.removeRow(row);
            }
        });
        
        //utility Panel
        utilPanel = new CustomPanel();
        utilPanel.setBackground(Color.DARK_GRAY);
        utilPanel.setPreferredSize(new Dimension(1500, 100));
        utilPane = new JScrollPane(utilPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        utilPane.setWheelScrollingEnabled(false);
        utilPane.setBounds(0, 0, 1500, 100);
        
        Borders borderSetter = new Borders();
        borderSetter.setBottomBorder(utilPane, Color.BLACK, 5);
        
        tableOption = new JLabel("Table Type:");
        tableOption.setBounds(10, 10, 180, 25);
        tableOption.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        tableOption.setForeground(Color.WHITE);
        
        addrmvLabel = new JLabel("Add/Remove Rows:");
        addrmvLabel.setBounds(10, 50, 180, 25);
        addrmvLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        addrmvLabel.setForeground(Color.WHITE);
        
        //Dropdown Box
        option = new JComboBox(new String[]{"First Come First Serve", "Shortest Job First", "Shortest Remaining Time", "Non Preemptive Priority", "Preemptive Priority", "Round Robin"});
        option.setBounds(110, 12, 166, 25);
        
        
        //Gantt Chart Panel
        chartPanel = new CustomPanel();
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setPreferredSize(new Dimension(2000, 100));
        chartPane = new JScrollPane(chartPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        chartPane.setBounds(25, 410, 450, 100);
        
        
        wtLabel = new JLabel("Average Waiting Time:");
        wtLabel.setBounds(25, 525, 180, 25);
        tatLabel = new JLabel("Average Turn Around Time:");
        tatLabel.setBounds(25, 550, 180, 25);
        wtResultLabel = new JLabel();
        wtResultLabel.setBounds(215, 525, 180, 25);
        tatResultLabel = new JLabel();
        tatResultLabel.setBounds(215, 550, 180, 25);       
        
        
        //Computation Button
        computeBtn = new JButton("Compute");
        computeBtn.setBounds(289, 12, 95, 25);
        computeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        computeBtn.addActionListener((ActionEvent e) -> {
            String selected = (String) option.getSelectedItem();
            CPUScheduler scheduler;
            
            switch (selected) {
                case "First Come First Serve":
                    scheduler = new FirstComeFirstServe();
                    break;
                case "Shortest Job First":
                    scheduler = new ShortestJobFirst();
                    break;
                case "Shortest Remaining Time":
                    scheduler = new ShortestRemainingTime();
                    break;
                case "Non Preemptive Priority":
                    scheduler = new PriorityNonPreemptive();
                    break;
                case "Preemptive Priority":
                    scheduler = new PriorityPreemptive();
                    break;
                case "Round Robin":
                    String tq = JOptionPane.showInputDialog("Time Quantum");
                    if (tq == null) {
                        return;
                    }
                    scheduler = new RoundRobin();
                    scheduler.setTimeQuantum(Integer.parseInt(tq));
                    break;
                default:
                    return;
            }
            
            for (int i = 0; i < model.getRowCount(); i++)
            {
                String process = (String) model.getValueAt(i, 0);
                int at = Integer.parseInt((String) model.getValueAt(i, 1));
                int bt = Integer.parseInt((String) model.getValueAt(i, 2));
                int pl;
                
                if (selected.equals("PSN") || selected.equals("PSP"))
                {
                    if (!model.getValueAt(i, 3).equals(""))
                    {
                        pl = Integer.parseInt((String) model.getValueAt(i, 3));
                    }
                    else
                    {
                        pl = 1;
                    }
                }
                else
                {
                    pl = 1;
                }
                
                scheduler.add(new Row(process, at, bt, pl));
            }
            
            scheduler.process();
            
            for (int i = 0; i < model.getRowCount(); i++)
            {
                String process = (String) model.getValueAt(i, 0);
                Row row = scheduler.getRow(process);
                model.setValueAt(row.getWaitingTime(), i, 4);
                model.setValueAt(row.getTurnaroundTime(), i, 5);
            }
            
            wtResultLabel.setText(Double.toString(scheduler.getAverageWaitingTime()));
            tatResultLabel.setText(Double.toString(scheduler.getAverageTurnAroundTime()));
            
            chartPanel.setTimeline(scheduler.getTimeline());
        });
   
        mainPanel = new JPanel(null);
        mainPanel.setPreferredSize(new Dimension(1500, 870));  
        
        //Utility Bar 
        mainPanel.add(tableOption);
        mainPanel.add(option);
        mainPanel.add(addrmvLabel);
        mainPanel.add(addBtn);
        mainPanel.add(removeBtn);
        mainPanel.add(computeBtn);
        mainPanel.add(utilPane);
        
        //Table
        mainPanel.add(tablePane);
        
        //GanttChart
        mainPanel.add(chartPane);
        mainPanel.add(wtLabel);
        mainPanel.add(tatLabel);
        
        //Result
        mainPanel.add(wtResultLabel);
        mainPanel.add(tatResultLabel);
        
        frame = new JFrame("AutoScheduler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.BLACK);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.add(mainPanel);
        frame.pack();
    }
    
    public static void main(String[] args)
    {
        new GUI();
    }
    
    class CustomPanel extends JPanel
    {   
        private List<Event> timeline;
        
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            
            if (timeline != null)
            {
//                int width = 30;
                
                for (int i = 0; i < timeline.size(); i++)
                {
                    Event event = timeline.get(i);
                    int x = 30 * (i + 1);
                    int y = 20;
                    
                    g.drawRect(x, y, 30, 30);
                    g.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    g.drawString(event.getProcessName(), x + 10, y + 20);
                    g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    g.drawString(Integer.toString(event.getStartTime()), x - 5, y + 45);
                    
                    if (i == timeline.size() - 1)
                    {
                        g.drawString(Integer.toString(event.getFinishTime()), x + 27, y + 45);
                    }
                    
//                    width += 30;
                }
                
//                this.setPreferredSize(new Dimension(width, 75));
            }
        }
        
        public void setTimeline(List<Event> timeline)
        {
            this.timeline = timeline;
            repaint();
        }
    }
}
