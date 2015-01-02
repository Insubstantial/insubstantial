package test;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel;

public class Walkthrough extends JFrame {
	public Walkthrough() {
		super("Sample app");
		this.setLayout(new FlowLayout());
		JButton button = new JButton("button");
		button.setMnemonic(KeyEvent.VK_B);
		this.add(button);
		this.add(new JCheckBox("check"));
		this.add(new JLabel("label"));

		this.setSize(new Dimension(250, 80));
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
            public void run() {
				try {

			        UIManager.setLookAndFeel(
			            UIManager.getSystemLookAndFeelClassName());
					UIManager.setLookAndFeel(new SubstanceGraphiteLookAndFeel());
				} catch (Exception e) {
					System.out
							.println("Substance Raven Graphite failed to initialize");
				}
				Walkthrough w = new Walkthrough();
				w.setVisible(true);
			}
		});
	}
}