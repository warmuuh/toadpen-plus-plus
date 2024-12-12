package wrm.toadpen.ai;

import io.avaje.inject.PostConstruct;
import jakarta.inject.Singleton;
import java.io.File;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class AiController {
  private final AiOptions aiOptions;
  private final AiCompletionProvider aiCompletionProvider;

  @PostConstruct
  public void init() {
    updateAiModel(aiOptions.getModelFile());
    aiOptions.onModelFileChanged(newModelFile -> {
      updateAiModel(newModelFile);
    });
  }

  private void updateAiModel(String newModelFile) {
    if (new File(newModelFile).exists()) {
      System.out.println("Loading Ai model: " + newModelFile);
      aiCompletionProvider.setAiModel(new AiModel(newModelFile));
    }
  }
}
