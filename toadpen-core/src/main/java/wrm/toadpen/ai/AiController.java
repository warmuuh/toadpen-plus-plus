package wrm.toadpen.ai;

import io.avaje.inject.PostConstruct;
import jakarta.inject.Singleton;
import java.io.File;
import lombok.RequiredArgsConstructor;
import wrm.toadpen.core.log.Logger;

@Singleton
@RequiredArgsConstructor
public class AiController {
  private final AiOptions aiOptions;
  private final AiCompletionProvider aiCompletionProvider;
  private final Logger logger;

  @PostConstruct
  public void init() {
    updateAiModel(aiOptions.getModelFile());
    aiOptions.onModelFileChanged(newModelFile -> {
      updateAiModel(newModelFile);
    });
  }

  private void updateAiModel(String newModelFile) {
    if (new File(newModelFile).exists()) {
      logger.info("Loading Ai model: " + newModelFile);
      aiCompletionProvider.setAiModel(new AiModel(newModelFile, logger));
    }
  }
}
