package wrm.toadpen.core.key;

import jakarta.inject.Singleton;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import lombok.RequiredArgsConstructor;
import wrm.toadpen.core.cmd.CommandManager;

@Singleton
@RequiredArgsConstructor
public class KeySetBinding {

  private final CommandManager commandManager;
  private final KeySet keySet;

  public void bindKeys(JFrame frame) {
    JPanel contentPane = (JPanel) frame.getContentPane();

    commandManager.getAllNoArgsCommands().forEach(command -> {
      var keyStroke = keySet.getKeystrokeForCommand(command.id());
      if (keyStroke != null) {
        contentPane.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, command.id());
        contentPane.getActionMap().put(command.id(), new AbstractAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            commandManager.executeCommandById(command.id());
          }
        });
      }
    });
  }
}
