package animu_minesweeper_ver2;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class OptionDialog extends JDialog {

	private static final long serialVersionUID = -3638366257883645109L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	
	OptionDialog(Field minesweeper) {

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle("\u041D\u0430\u0441\u0442\u0440\u043E\u0439\u043A\u0438");
		setBounds(100, 100, 250, 180);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JLabel label = new JLabel("\u041A-\u0432\u043E \u043A\u043B\u0435\u0442\u043E\u043A");
		label.setBounds(10, 40, 75, 14);
		contentPanel.add(label);

		textField = new JTextField(String.valueOf(minesweeper.getWidth()));
		textField.setBounds(106, 37, 50, 20);
		contentPanel.add(textField);
		textField.setColumns(10);

		textField_1 = new JTextField(String.valueOf(minesweeper.getHeight()));
		textField_1.setBounds(176, 37, 50, 20);
		contentPanel.add(textField_1);
		textField_1.setColumns(10);

		JLabel lblX_1 = new JLabel("x");
		lblX_1.setBounds(96, 40, 25, 14);
		contentPanel.add(lblX_1);

		JLabel lblY = new JLabel("y");
		lblY.setBounds(166, 40, 25, 14);
		contentPanel.add(lblY);

		JLabel label_3 = new JLabel("\u041A-\u0432\u043E \u043C\u0438\u043D");
		label_3.setBounds(10, 80, 75, 14);
		contentPanel.add(label_3);

		textField_2 = new JTextField(String.valueOf(minesweeper.getCount()));
		textField_2.setBounds(106, 77, 50, 20);
		contentPanel.add(textField_2);
		textField_2.setColumns(10);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener((ActionEvent e) -> {
					try {
						int h = Integer.parseInt(textField_1.getText());
						int w = Integer.parseInt(textField.getText());
						int c = Integer.parseInt(textField_2.getText());
						if (h > 3 && w > 3 && c > 0 && c < h * w && h <= 20 && w <= 20) {
							if (h != minesweeper.getHeight() || w != minesweeper.getWidth()
									|| c != minesweeper.getCount()) {
								Memesweeper.frameReset();
								Memesweeper.init(h, w, c);
							}
							this.dispose();
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(minesweeper.getGui(), ex.getMessage());
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("\u041E\u0442\u043C\u0435\u043D\u0430");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener((ActionEvent e) -> {
					this.dispose();
				});
				buttonPane.add(cancelButton);
			}
		}

		setVisible(true);

	}

}
