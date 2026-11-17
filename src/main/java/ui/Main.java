package ui;

import java.awt.*;
import java.awt.event.*;
import java.nio.file.*;
import javax.swing.*;
import model.service.Simulation;
import model.io.*;
import model.domain.*;
import model.domain.Types.*;
import model.structures.LinkedList;

/** MVC View/Controller: all domain logic resides in Simulation. */
public class Main extends JFrame {
    private Simulation model;
    private final JLabel summary=new JLabel(),priority=new JLabel(),fifo=new JLabel(),last=new JLabel(),coverage=new JLabel();
    private final JTextArea log=new JTextArea(11,42);
    private final DefaultListModel<String> incidentRows=new DefaultListModel<>(),vehicleRows=new DefaultListModel<>();
    private final JList<String> incidentList=new JList<>(incidentRows),vehicleList=new JList<>(vehicleRows);
    private final JPanel cards=new JPanel(new CardLayout());
    private final CityMap map=new CityMap();
    private final Path savePath=Path.of("state","simulation.ser");
    private final java.util.concurrent.ScheduledExecutorService scheduler=java.util.concurrent.Executors.newSingleThreadScheduledExecutor(r->{Thread t=new Thread(r,"sgmms-simulation");t.setDaemon(true);return t;});
    private boolean showCorridors=false;
    public Main(Simulation initial){super("SGMMS | Palmira Municipal Monitoring");model=initial;setDefaultCloseOperation(EXIT_ON_CLOSE);setSize(1100,760);setLocationRelativeTo(null);
        JPanel navbar=new JPanel(new FlowLayout(FlowLayout.LEADING));button(navbar,"Monitoring",()->show("MONITOR"));button(navbar,"Traffic Map",()->show("MAP"));button(navbar,"Incidents",()->show("INCIDENTS"));
        button(navbar,"Save",()->safe(()->{StateStore.save(model,savePath);message("Simulation saved");}));button(navbar,"Load",()->safe(()->{model=StateStore.load(savePath);message("Simulation recovered");refresh();}));
        add(navbar,BorderLayout.NORTH);
        JPanel monitor=new JPanel(new BorderLayout(15,15));monitor.setBorder(BorderFactory.createEmptyBorder(24,24,24,24));
        JPanel top=new JPanel(new GridLayout(5,1,5,5));top.add(new JLabel("SGMMS — Municipal Emergency Operations Center",SwingConstants.CENTER));top.add(summary);top.add(priority);top.add(fifo);top.add(coverage);monitor.add(top,BorderLayout.NORTH);
        log.setEditable(false);monitor.add(new JScrollPane(log),BorderLayout.CENTER);cards.add(monitor,"MONITOR");
        JPanel mapScreen=new JPanel(new BorderLayout());mapScreen.add(map,BorderLayout.CENTER);JPanel mapButtons=new JPanel();
        button(mapButtons,"↑ W",()->safe(()->model.move(0,-1)));button(mapButtons,"← A",()->safe(()->model.move(-1,0)));
        button(mapButtons,"↓ S",()->safe(()->model.move(0,1)));button(mapButtons,"→ D",()->safe(()->model.move(1,0)));button(mapButtons,"Undo U",()->safe(model::undo));
        button(mapButtons,"Emergency corridors",()->{showCorridors=!showCorridors;map.repaint();});
        button(mapButtons,"Block road",()->modifyRoad(true));button(mapButtons,"Unblock road",()->modifyRoad(false));
        mapScreen.add(mapButtons,BorderLayout.SOUTH);cards.add(mapScreen,"MAP");
        JPanel incidents=new JPanel(new BorderLayout(10,10));JSplitPane split=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,new JScrollPane(incidentList),new JScrollPane(vehicleList));split.setResizeWeight(.62);incidents.add(split,BorderLayout.CENTER);
        JPanel incidentTop=new JPanel(new GridLayout(3,1));incidentTop.add(priority); // shared JLabel will be relocated below, dedicated header instead
        incidentTop.remove(priority);incidentTop.add(new JLabel("Incidents (ordered by severity, then oldest first)",SwingConstants.CENTER));incidentTop.add(last);incidentTop.add(new JLabel("Select an incident and a vehicle, then Assign. Or use Suggested vehicle."));incidents.add(incidentTop,BorderLayout.NORTH);
        JPanel actions=new JPanel();button(actions,"Suggest & Assign",()->safe(()->{String id=selectedIncident();Vehicle v=model.recommend(id);if(v==null)throw new DomainException("No compatible available vehicle");model.assign(id,v.id);}));
        button(actions,"Assign selected",()->safe(()->{String selected=vehicleList.getSelectedValue();if(selected==null)throw new DomainException("Select a vehicle");model.assign(selectedIncident(),selected.split(" ")[0]);}));
        button(actions,"Resolve",()->safe(()->message("Awarded "+model.resolve(selectedIncident())+" points")));
        button(actions,"Create incident",()->createIncident());button(actions,"Resolve highest",()->safe(()->{Incident h=model.highest();if(h==null)throw new DomainException("No pending incident");if(h.status==IncidentStatus.PENDING){Vehicle v=model.recommend(h.id);if(v==null)throw new DomainException("No vehicle available");model.assign(h.id,v.id);}message("Awarded "+model.resolve(h.id)+" points");}));
        incidents.add(actions,BorderLayout.SOUTH);cards.add(incidents,"INCIDENTS");add(cards,BorderLayout.CENTER);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e->{if(e.getID()!=KeyEvent.KEY_PRESSED||!map.isShowing()||e.getSource() instanceof JTextField)return false;int code=e.getKeyCode();try{switch(code){case KeyEvent.VK_W,KeyEvent.VK_UP -> model.move(0,-1);case KeyEvent.VK_S,KeyEvent.VK_DOWN -> model.move(0,1);case KeyEvent.VK_A,KeyEvent.VK_LEFT -> model.move(-1,0);case KeyEvent.VK_D,KeyEvent.VK_RIGHT -> model.move(1,0);case KeyEvent.VK_U -> model.undo();default -> {return false;}}refresh();}catch(RuntimeException ex){message(ex.getMessage());}return true;});
        scheduler.scheduleAtFixedRate(()->{try{model.tick();SwingUtilities.invokeLater(this::refresh);}catch(RuntimeException ex){SwingUtilities.invokeLater(()->message("Simulation error: "+ex.getMessage()));}},2500,2500,java.util.concurrent.TimeUnit.MILLISECONDS);refresh();
    }
    @Override public void dispose(){scheduler.shutdownNow();super.dispose();}
    private void button(JPanel p,String name,Runnable action){JButton button=new JButton(name);button.addActionListener(e->action.run());p.add(button);}
    private void show(String card){((CardLayout)cards.getLayout()).show(cards,card);refresh();}
    private void safe(Throwing task){try{task.run();refresh();}catch(Exception e){message("Error: "+e.getMessage());}}
    private interface Throwing {void run()throws Exception;}
    private void message(String message){log.append(message+"\n");log.setCaretPosition(log.getDocument().getLength());}
    private String selectedIncident(){String selected=incidentList.getSelectedValue();if(selected==null)throw new DomainException("Select an incident");return selected.split(" ")[0];}
    private void createIncident(){JComboBox<IncidentType> types=new JComboBox<>(IncidentType.values());JComboBox<Severity> severities=new JComboBox<>(Severity.values());JTextField location=new JTextField("0");JPanel fields=new JPanel(new GridLayout(3,2));fields.add(new JLabel("Type"));fields.add(types);fields.add(new JLabel("Severity"));fields.add(severities);fields.add(new JLabel("Road cell number"));fields.add(location);
        if(JOptionPane.showConfirmDialog(this,fields,"New incident",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)safe(()->model.register((IncidentType)types.getSelectedItem(),(Severity)severities.getSelectedItem(),Integer.parseInt(location.getText()),"Registered by operator"));}
    private void modifyRoad(boolean block){String input=JOptionPane.showInputDialog(this,"Two adjacent vertex IDs separated by comma (example: 1,2)");if(input==null)return;safe(()->{String[] parts=input.split(",");int a=Integer.parseInt(parts[0].trim()),b=Integer.parseInt(parts[1].trim());if(block)model.blockRoad(a,b);else model.unblockRoad(a,b);});}
    private void refresh(){summary.setText("Score: "+model.score()+"  |  Active: "+model.activeCount()+"  |  Accidents: "+model.countType(IncidentType.ACCIDENT)+"  |  Robberies: "+model.countType(IncidentType.ROBBERY)+"  |  Fires: "+model.countType(IncidentType.FIRE)+"  |  Available vehicles: "+model.availableCount());
        Incident highest=model.highest();priority.setText("Highest priority: "+(highest==null?"none":highest));Incident arriving=model.nextArrival();fifo.setText("FIFO dispatch: "+(arriving==null?"none":arriving.id));
        coverage.setText("Monitoring coverage: "+model.graph().bfs(model.position()).size()+" / "+model.graph().size()+" road cells  |  Connected components: "+model.components()+"  |  Cycles: "+model.hasCycle());
        OperatorAction action=model.lastAction();last.setText("Last management action: "+(action==null?"none":action.description()));
        String incidentSelection=incidentList.getSelectedValue(),vehicleSelection=vehicleList.getSelectedValue();incidentRows.clear();vehicleRows.clear();for(Incident i:model.activeIncidents())incidentRows.addElement(i.toString()+" | vehicle "+(i.vehicleId==null?"none":i.vehicleId));for(Vehicle v:model.allVehicles())vehicleRows.addElement(v.toString());if(incidentSelection!=null)incidentList.setSelectedValue(incidentSelection,true);if(vehicleSelection!=null)vehicleList.setSelectedValue(vehicleSelection,true);
        SimulationEvent event;while((event=model.pollEvent())!=null)message(event.description());map.repaint();}
    private class CityMap extends JPanel {
        CityMap(){setBackground(new Color(235,243,250));setPreferredSize(new Dimension(800,550));}
        @Override protected void paintComponent(Graphics graphic){super.paintComponent(graphic);Graphics2D g=(Graphics2D)graphic.create();int s=Math.min((getWidth()-60)/model.width(),(getHeight()-60)/model.height());s=Math.max(10,s);int ox=25,oy=25;
            for(int i=0;i<model.width()*model.height();i++){int x=ox+(i%model.width())*s,y=oy+(i/model.width())*s;g.setColor(switch(model.cell(i)){case "#" -> new Color(81,88,94);case "R" -> new Color(166,218,164);case "C" -> new Color(255,215,139);case "M" -> new Color(176,198,215);default -> Color.WHITE;});g.fillRect(x,y,s,s);g.setColor(new Color(197,205,214));g.drawRect(x,y,s,s);g.setColor(Color.DARK_GRAY);g.setFont(new Font("SansSerif",Font.PLAIN,Math.max(9,s/5)));g.drawString(""+i,x+3,y+12);}
            if(showCorridors){g.setStroke(new BasicStroke(4));g.setColor(new Color(34,125,206,155));for(var e:model.corridors().edges())line(g,ox,oy,s,e.from(),e.to());}
            for(Vehicle v:model.allVehicles()){int x=ox+(v.location%model.width())*s+s/2,y=oy+(v.location/model.width())*s+s/2;g.setColor(v.type==VehicleType.AMBULANCE?Color.RED:v.type==VehicleType.FIRE_TRUCK?Color.ORANGE:Color.BLUE);g.fillOval(x-s/5,y-s/5,s*2/5,s*2/5);g.setColor(Color.BLACK);g.drawString(v.id,x-s/4,y+s/3);if(v.incidentId!=null){try{Incident i=model.incident(v.incidentId);g.setColor(new Color(0,80,180));g.setStroke(new BasicStroke(2));for(Integer p:model.route(i.id)){int px=ox+(p%model.width())*s+s/2,py=oy+(p/model.width())*s+s/2;g.drawOval(px-3,py-3,6,6);}}catch(RuntimeException ignored){}}}
            for(Incident i:model.activeIncidents()){int x=ox+(i.location%model.width())*s+s/2,y=oy+(i.location/model.width())*s+s/2;g.setColor(new Color(220,35,45));g.fillRect(x-s/7,y-s/7,s*2/7,s*2/7);g.setColor(Color.BLACK);g.drawString("!",x-2,y+4);}
            int p=model.position();g.setColor(new Color(115,29,156));g.setStroke(new BasicStroke(3));g.drawOval(ox+(p%model.width())*s+s/4,oy+(p/model.width())*s+s/4,s/2,s/2);g.dispose();}
        private void line(Graphics2D g,int ox,int oy,int s,int a,int b){g.drawLine(ox+(a%model.width())*s+s/2,oy+(a/model.width())*s+s/2,ox+(b%model.width())*s+s/2,oy+(b/model.width())*s+s/2);}
    }
    public static void main(String[] args){SwingUtilities.invokeLater(()->{try{Simulation simulation=ConfigurationLoader.load(Path.of("src/main/resources"));new Main(simulation).setVisible(true);}catch(Exception e){JOptionPane.showMessageDialog(null,"Could not start SGMMS: "+e.getMessage(),"Configuration error",JOptionPane.ERROR_MESSAGE);e.printStackTrace();}});}
}
