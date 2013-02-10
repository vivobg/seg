package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import map.Map;

public class MapChange extends JFrame{
	private Map map;
	
	public MapChange(Map map){
		super("Map Change");
		this.map = map;
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initWidgets();
	}

	private void initWidgets() {
		final JTextField jtX = new JTextField(4);
		final JTextField jtY = new JTextField(4);
		final JTextField jtV = new JTextField(4);
		JButton jbtn = new JButton("Update Map");
		this.setLayout(new GridLayout(4, 2));
		this.add(new JLabel("X: "));
		this.add(jtX);
		this.add(new JLabel("Y: "));
		this.add(jtY);
		this.add(new JLabel("Value: "));
		this.add(jtV);
		
		this.add(jbtn);
		this.pack();
		jbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int x = Integer.valueOf(jtX.getText());
				int y = Integer.valueOf(jtY.getText());
				float v = Float.valueOf(jtV.getText());
				map.setValue(x, y, v);
				
			}
		});
		
	}

}
