# 📁 File Translator Copier

A simple Java tool that:

- Scans the `input/` folder (including subfolders)  
- Translates Ukrainian filenames to English using the OpenAI API  
- Copies renamed `.txt` versions to `output/{timestamp}/` while preserving folder structure

---

## 🚀 How to Run

Make sure to set your OpenAI API key in `OpenAIManager.java`.

Run the app:

```bash
./gradlew run
