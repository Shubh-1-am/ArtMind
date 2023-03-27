
# ArtMind
ArtMind is an Android application that uses OpenAI's GPT-3 to create unique and high-quality images based on user prompts, with future features for image editing and variation generation

## Getting Started

1. Clone the repository.
2. Generate an OpenAI API key from the [OpenAI website](https://platform.openai.com/).
3. Open the **build.gradle** file and replace the "<YOUR_API_KEY>" value for **OPENAI_API_KEY** with your actual API key:

```
defaultConfig {
    //...
    buildConfigField("String", "OPENAI_API_KEY", "\"<YOUR_API_KEY>\"")
}

```
4. Build and run the app.


## Features

- Enter a prompt and generate an image.
- Specify the number of images to generate.
- Save the generated images to your device's gallery.
- View the history of generated images 
- Offline mode support: generated images will be saved locally and can be viewed and downloaded without an internet connection


