
package gui;

/**
 * 
 * @author Vilian
 */
public class NBGui extends javax.swing.JFrame {

	/**
	 * Creates new form NBGui
	 */
	public NBGui() {
		initComponents();
	}

	private void initComponents() {

		btnGrpMode = new javax.swing.ButtonGroup();
		jpMainPanel = new javax.swing.JPanel();
		jpMode = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jRadSolo = new javax.swing.JRadioButton();
		jRadMulti = new javax.swing.JRadioButton();
		jbtnExplore = new javax.swing.JButton();
		jpCollection = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		jpCollectionArea = new javax.swing.JPanel();
		jLabel3 = new javax.swing.JLabel();
		jtX1 = new javax.swing.JTextField();
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		jtX2 = new javax.swing.JTextField();
		jtY1 = new javax.swing.JTextField();
		jtY2 = new javax.swing.JTextField();
		jbtnCollect = new javax.swing.JButton();
		jpSaveMap = new javax.swing.JPanel();
		jLabel7 = new javax.swing.JLabel();
		jLabel8 = new javax.swing.JLabel();
		jtFilename = new javax.swing.JTextField();
		jLabel9 = new javax.swing.JLabel();
		jbtnSave = new javax.swing.JButton();
		jpConsole = new javax.swing.JPanel();
		jLabel10 = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jtConsole = new javax.swing.JTextPane();
		jscrMap = new javax.swing.JScrollPane();
		jMapPanel = new javax.swing.JPanel();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Team Babbage SEG Group Project");

		jpMainPanel.setBackground(new java.awt.Color(255, 0, 0));

		jLabel1.setText("ROBOT MODE");

		btnGrpMode.add(jRadSolo);
		jRadSolo.setSelected(true);
		jRadSolo.setText("SOLO");
		jRadSolo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadSoloActionPerformed(evt);
			}
		});

		btnGrpMode.add(jRadMulti);
		jRadMulti.setText("MULTI");
		jRadMulti.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadMultiActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jpModeLayout = new javax.swing.GroupLayout(
				jpMode);
		jpMode.setLayout(jpModeLayout);
		jpModeLayout
				.setHorizontalGroup(jpModeLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jpModeLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jpModeLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jRadSolo)
														.addComponent(jLabel1)
														.addComponent(jRadMulti))
										.addGap(191, 191, 191)));

		jpModeLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
				new java.awt.Component[] { jRadMulti, jRadSolo });

		jpModeLayout
				.setVerticalGroup(jpModeLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jpModeLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(jLabel1)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(jRadSolo)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(jRadMulti)
										.addContainerGap(15, Short.MAX_VALUE)));

		jbtnExplore.setText("EXPLORE");
		jbtnExplore.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jbtnExploreActionPerformed(evt);
			}
		});

		jLabel2.setText("COLLECTION");

		jLabel3.setText("x1:");

		jtX1.setText("1.2");

		jLabel4.setText("x2:");

		jLabel5.setText("y1:");

		jLabel6.setText("y2:");

		jtX2.setText("2.3");

		jtY1.setText("3.4");

		jtY2.setText("4.5");

		javax.swing.GroupLayout jpCollectionAreaLayout = new javax.swing.GroupLayout(
				jpCollectionArea);
		jpCollectionArea.setLayout(jpCollectionAreaLayout);
		jpCollectionAreaLayout
				.setHorizontalGroup(jpCollectionAreaLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jpCollectionAreaLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jpCollectionAreaLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addGroup(
																jpCollectionAreaLayout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel4)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jtX2,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				71,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(
																jpCollectionAreaLayout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel3)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jtX1)))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jpCollectionAreaLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jLabel5)
														.addComponent(jLabel6))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jpCollectionAreaLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addComponent(
																jtY1,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																57,
																Short.MAX_VALUE)
														.addComponent(jtY2))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		jpCollectionAreaLayout
				.linkSize(javax.swing.SwingConstants.HORIZONTAL,
						new java.awt.Component[] { jLabel3, jLabel4, jLabel5,
								jLabel6 });

		jpCollectionAreaLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
				new java.awt.Component[] { jtX1, jtX2, jtY1, jtY2 });

		jpCollectionAreaLayout
				.setVerticalGroup(jpCollectionAreaLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jpCollectionAreaLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jpCollectionAreaLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel3)
														.addComponent(
																jtX1,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel5)
														.addComponent(
																jtY1,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jpCollectionAreaLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																jtX2,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel4)
														.addComponent(jLabel6)
														.addComponent(
																jtY2,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		jpCollectionAreaLayout
				.linkSize(javax.swing.SwingConstants.VERTICAL,
						new java.awt.Component[] { jLabel3, jLabel4, jLabel5,
								jLabel6 });

		jbtnCollect.setText("COLLECT");
		jbtnCollect.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jbtnCollectActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jpCollectionLayout = new javax.swing.GroupLayout(
				jpCollection);
		jpCollection.setLayout(jpCollectionLayout);
		jpCollectionLayout
				.setHorizontalGroup(jpCollectionLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jpCollectionLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jpCollectionLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jpCollectionArea,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addGroup(
																jpCollectionLayout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel2)
																		.addGap(0,
																				0,
																				Short.MAX_VALUE))
														.addComponent(
																jbtnCollect,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addContainerGap()));
		jpCollectionLayout
				.setVerticalGroup(jpCollectionLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jpCollectionLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(jLabel2)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jpCollectionArea,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jbtnCollect)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		jLabel7.setText("SAVE MAP");

		jLabel8.setText("Filename:");

		jtFilename.setText("map1");

		jLabel9.setText(".png");

		jbtnSave.setText("SAVE");
		jbtnSave.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jbtnSaveActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jpSaveMapLayout = new javax.swing.GroupLayout(
				jpSaveMap);
		jpSaveMap.setLayout(jpSaveMapLayout);
		jpSaveMapLayout
				.setHorizontalGroup(jpSaveMapLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jpSaveMapLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jpSaveMapLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jpSaveMapLayout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel8)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jtFilename)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jLabel9))
														.addComponent(
																jbtnSave,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addGroup(
																jpSaveMapLayout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel7)
																		.addGap(0,
																				0,
																				Short.MAX_VALUE)))
										.addContainerGap()));
		jpSaveMapLayout
				.setVerticalGroup(jpSaveMapLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jpSaveMapLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(jLabel7)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jpSaveMapLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel8)
														.addComponent(
																jtFilename,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel9))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jbtnSave)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		jLabel10.setText("CONSOLE");

		jtConsole.setEditable(false);
		jtConsole.setContentType("text/html"); // NOI18N
		jtConsole
				.setText("<html>\n  <head>\n\n  </head>\n  <body>\n    <p style=\"margin-top: 0\">\n     $: <b>Bold</b> <i>Italic</i><br>\n     $: <span color=\"red\">Red</span>\n    </p>\n  </body>\n</html>\n");
		jScrollPane1.setViewportView(jtConsole);

		javax.swing.GroupLayout jpConsoleLayout = new javax.swing.GroupLayout(
				jpConsole);
		jpConsole.setLayout(jpConsoleLayout);
		jpConsoleLayout
				.setHorizontalGroup(jpConsoleLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jpConsoleLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jpConsoleLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jScrollPane1)
														.addGroup(
																jpConsoleLayout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel10)
																		.addGap(0,
																				0,
																				Short.MAX_VALUE)))
										.addContainerGap()));
		jpConsoleLayout
				.setVerticalGroup(jpConsoleLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jpConsoleLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(jLabel10)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jScrollPane1)
										.addContainerGap()));

		javax.swing.GroupLayout jpMainPanelLayout = new javax.swing.GroupLayout(
				jpMainPanel);
		jpMainPanel.setLayout(jpMainPanelLayout);
		jpMainPanelLayout
				.setHorizontalGroup(jpMainPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								jpMainPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jpMainPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																jpConsole,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																jpSaveMap,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																jpMode,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																jbtnExplore,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																jpCollection,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addContainerGap()));
		jpMainPanelLayout
				.setVerticalGroup(jpMainPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jpMainPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												jpMode,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jbtnExplore)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jpCollection,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jpSaveMap,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jpConsole,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addContainerGap()));

		jscrMap.setBackground(new java.awt.Color(128, 128, 128));
		jscrMap.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		jscrMap.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		jMapPanel.setBackground(new java.awt.Color(128, 128, 128));

		javax.swing.GroupLayout jMapPanelLayout = new javax.swing.GroupLayout(
				jMapPanel);
		jMapPanel.setLayout(jMapPanelLayout);
		jMapPanelLayout.setHorizontalGroup(jMapPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 572,
				Short.MAX_VALUE));
		jMapPanelLayout.setVerticalGroup(jMapPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 670,
				Short.MAX_VALUE));

		jscrMap.setViewportView(jMapPanel);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jpMainPanel,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jscrMap,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										575, Short.MAX_VALUE).addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.TRAILING)
												.addComponent(
														jscrMap,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														673, Short.MAX_VALUE)
												.addComponent(
														jpMainPanel,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE))
								.addContainerGap()));

		pack();
	}// </editor-fold>

	private void jbtnExploreActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}

	private void jRadSoloActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}

	private void jRadMultiActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}

	private void jbtnCollectActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}

	private void jbtnSaveActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		// <editor-fold defaultstate="collapsed"
		// desc=" Look and feel setting code (optional) ">
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the
		 * default look and feel. For details see
		 * http://download.oracle.com/javase
		 * /tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
					.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(NBGui.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(NBGui.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(NBGui.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(NBGui.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		}
		// </editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new NBGui().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify
	private javax.swing.ButtonGroup btnGrpMode;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel10;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JPanel jMapPanel;
	private javax.swing.JRadioButton jRadMulti;
	private javax.swing.JRadioButton jRadSolo;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JButton jbtnCollect;
	private javax.swing.JButton jbtnExplore;
	private javax.swing.JButton jbtnSave;
	private javax.swing.JPanel jpCollection;
	private javax.swing.JPanel jpCollectionArea;
	private javax.swing.JPanel jpConsole;
	private javax.swing.JPanel jpMainPanel;
	private javax.swing.JPanel jpMode;
	private javax.swing.JPanel jpSaveMap;
	private javax.swing.JScrollPane jscrMap;
	private javax.swing.JTextPane jtConsole;
	private javax.swing.JTextField jtFilename;
	private javax.swing.JTextField jtX1;
	private javax.swing.JTextField jtX2;
	private javax.swing.JTextField jtY1;
	private javax.swing.JTextField jtY2;
	// End of variables declaration
}
