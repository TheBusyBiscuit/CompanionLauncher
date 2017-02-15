package io.github.TheBusyBiscuit.CompanionLauncher.components;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import io.github.TheBusyBiscuit.CompanionLauncher.AppInfo;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.DiskFormat;

public class ProgressDialog extends JDialog {
	
	private static final long serialVersionUID = 52815960796420785L;
	
	public JFrame frame;
	public JProgressBar progressbar;
	public JLabel label;
	
	public ProgressDialog(JFrame frame, boolean visible) {
		super(frame, AppInfo.name + " v" + AppInfo.version, false);
		
		this.frame = frame;
		progressbar = new JProgressBar(0, 1);
		label = new JLabel("Please wait...");

		add(BorderLayout.CENTER, progressbar);
		add(BorderLayout.NORTH, label);
		setSize(360, 70);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setAlwaysOnTop(true);
		
		setVisible(visible);
	}

	@Override
	public void dispose() {
		System.exit(0);
		super.dispose();
	}
	
	public void addMaximum(int n) {
		progressbar.setMaximum(progressbar.getMaximum() + n);
	}
	
	public void addProgress() {
		progressbar.setValue(progressbar.getValue() + 1);
		
		double percent = DiskFormat.fix((progressbar.getValue() * 100.0) / progressbar.getMaximum());
		
		label.setText(percent + "% (" + progressbar.getValue() + " / " + progressbar.getMaximum() + ")");
		
		if (progressbar.getValue() >= progressbar.getMaximum()) {
			super.dispose();
		}
	}

}
