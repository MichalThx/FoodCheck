# Food-Check
### Recipe finder Android App with Machine Learning image identification of ingredients based on the InceptionV3

### Screenshots

### How does it work?

The app has to main goals: add ingredients to the "I have them in a fridge list" and find recipes that can be used within that list.

The ingredient listing has a Local DB in the backend combined with InceptionV3 model used for identifying set of predefined ingredients.

The recipe base is a mix of local and remote one, firstly it will try to match the most fitting recipe within the already stored recipe. If those actions fail, the app will connect remote API and store the chosen recipe on the phone. 

