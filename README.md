# vne
Native extensions created for Vis project.

Current release version: 0.0.1

#### runtime
Shared library loader, required by all extensions.

```groovy
compile "com.kotcrab.vne:vne-runtime:$vneVersion"
```

#### win-thumbnails
Fast and memory efficient thumbnail provider using WinAPI to extract thumbnails from images. Works on Windows Vista and newer. 
Internally uses `IThumbnailCache`. 

```groovy
compile "com.kotcrab.vne:vne-win-thumbnails:$vneVersion"
```
