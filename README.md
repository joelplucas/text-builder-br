# text-builder-br
- Generating Brazilian authors text (my first steps with deep learning)
- This tool was used to make a demo at JavaOne Latin America 2016
- It is example was inspired by Andrej Karpathy's blog post "The Unreasonable Effectiveness of Recurrent Neural Networks" http://karpathy.github.io/2015/05/21/rnn-effectiveness/
- The project consists of a LSTM (Long Short Term Memory) RNN (Recurrent Neural Network) to generate text according to Brazillian literature authors writting style. 
- The code uses the deeplearning4j library.

# Training data set
You may find three brazillian authors (Fernando Pessoa, Veredas and Carlos Dummond de Andrade) )texts in txt files in the "resources" directory. Each file corresponds to an author. For each author, the texts were extracted from PDF files and then consolidated in a single txt file.

# RNN Training
Each author has to be trained separatedly by means of the "LSTMBRTextModellingTrainer" class. The following input parameters are needed:
- Txt file path for training. A training dataset is already availabe in the "resources" directory
- Minimun batch size: integer (optional parameter. Dafault = 32)
- Example length: integer (optional parameter. Default - 1000)

# Running GUI for generating text
You may run the graphical interface, where you can chosse one of the three authors, for generating a random text by runing the "TextGeneratorGUIApp" class. The input parameters are the same of the the "LSTMBRTextModellingTrainer" class.
