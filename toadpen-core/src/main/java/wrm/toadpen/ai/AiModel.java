package wrm.toadpen.ai;

import de.kherud.llama.InferenceParameters;
import de.kherud.llama.LlamaIterator;
import de.kherud.llama.LlamaModel;
import de.kherud.llama.LlamaOutput;
import de.kherud.llama.ModelParameters;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AiModel {

  private boolean isModelLoaded = false;
  private ModelParameters modelParams;
  private LlamaModel model;
  private final ExecutorService executorService = Executors.newSingleThreadExecutor();

  private final String prompt = "given the following code fragment, " +
      "propose some code that can replace the $$$ in the fragment. " +
      "only respond with the proposal itself and nothing else. the fragment: \n\n";

  public AiModel(String pathToGgufModelFile) {
    executorService.submit(() -> {
      modelParams = new ModelParameters()
          .setModelFilePath(pathToGgufModelFile)
          .setNGpuLayers(43);
      model = new LlamaModel(modelParams);
      isModelLoaded = true;
    });
  }

  public String ask(String codeFragment) {
    String finalPrompt = prompt + codeFragment;
    System.out.println("Prompt: " + finalPrompt);
    String[] split = codeFragment.split("\\$\\$\\$");
    InferenceParameters inferParams = new InferenceParameters("")
        .setInputPrefix(split[0])
        .setInputSuffix(split[1]);

    StringBuilder b = new StringBuilder();
    System.out.println("Generating completions...");
    for (LlamaIterator iterator = model.generate(inferParams).iterator(); iterator.hasNext(); ) {
      LlamaOutput output = iterator.next();
      if (output.toString().trim().equals("<EOT>")) {
        iterator.cancel();
        break;
      }
      b.append(output);
    }
    return b.toString();
  }

  public boolean isModelLoaded() {
    return isModelLoaded;
  }
}
