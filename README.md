# vne
Native extensions created for Vis project. Made to be modular as much as possible and can be used in any Java application.

Current release version: 1.0.0

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

Usage example:
```java
WinThumbnailProvider provider = new WinThumbnailProvider();

// request 200px thumbnail, returned array contains pixels in RGBA8888 format with two first elements being size of image
// see Javadoc for more details
int[] data = provider.getThumbnail("C:\\Path\\To\\Image", 200);

provider.dispose()
```
