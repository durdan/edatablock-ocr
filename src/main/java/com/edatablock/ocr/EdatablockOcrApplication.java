package com.edatablock.ocr;


import com.sun.jna.Pointer;
import net.sourceforge.lept4j.Leptonica;
import net.sourceforge.lept4j.Pix;
import net.sourceforge.tess4j.*;
import net.sourceforge.tess4j.util.ImageIOHelper;
import net.sourceforge.tess4j.util.LoggHelper;
import net.sourceforge.tess4j.util.PdfUtilities;
import net.sourceforge.tess4j.util.Utils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static net.sourceforge.tess4j.ITessAPI.TRUE;

//@ImportResource({"classpath:test-data/*.*" })
@SpringBootApplication
public class EdatablockOcrApplication {

    private static final Logger logger = LoggerFactory.getLogger(new LoggHelper().toString());
    private Resource resource;
    static File imageFile = new File("/Users/durdan/devel/projects/edatablock-ocr/src/main/resources/test-data/INVOICE 2312874115.pdf");
    public static void main(String[] args) {
        SpringApplication.run(EdatablockOcrApplication.class, args);

       // GetWordsWithZone getWordsWithZone = new GetWordsWithZone();

        try {
            //getWordsWithZone.testGetWords();
            getWordWithZone();
            getResult();
            //getWordsWithZone.PdfToImage("/Users/durdan/devel/projects/edatablock-ocr/src/main/resources/test-data/INVOICE 2312874115.pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        getWordWithZone();
//        try {
//            getBaseAPIAnalyseLayout();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        getResult();
//        try {
//            testOSD();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        final String datapath = "/Users/durdan/devel/projects/edatablock-ocr/src/main/resources/tessdata/";
//        final String testResourcesDataPath = "";
//        String language = "eng";
//        String expOCRResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
//
//
//        logger.info("TessBaseAPIGetUTF8Text");
//        TessAPI api;
//        ITessAPI.TessBaseAPI handle;
//        api =TessAPI.INSTANCE;
//        handle = api.TessBaseAPICreate();
//
//
//        String expResult = expOCRResult;
//        File tiff = new File("/Users/durdan/devel/projects/edatablock-ocr/src/main/resources/test-data/AWB 2312874115.pdf");
//        BufferedImage image = null; // require jai-imageio lib to read TIFF
//        try {
//            image = ImageIO.read(new FileInputStream(tiff));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        ByteBuffer buf = ImageIOHelper.convertImageData(image);
//        int bpp = image.getColorModel().getPixelSize();
//        int bytespp = bpp / 8;
//        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
//        api.TessBaseAPIInit3(handle, datapath, language);
//        api.TessBaseAPISetPageSegMode(handle, ITessAPI.TessPageSegMode.PSM_AUTO);
//        api.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
//        //Rectangle rect = new Rectangle(1321, 16, 1439, 55);
//
//        api.TessBaseAPISetRectangle(handle, 1321, 16, 1439, 55);
//        Pointer utf8Text = api.TessBaseAPIGetUTF8Text(handle);
//        String result = utf8Text.getString(0);
//        api.TessDeleteText(utf8Text);
//        logger.info(result);
    }

    public static void getResult(){
        String datapath = "/usr/local/Cellar/tesseract/3.05.01/share/";
        String language = "eng";
        ITessAPI.TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        //////
        //load PostScript document


        //System.setProperty(PdfUtilities.PDF_LIBRARY, PdfUtilities.PDFBOX);
        ////// 1321 2140 16 55

        File pdf = new File("/Users/durdan/devel/projects/edatablock-ocr/src/main/resources/test-data/1.png");
        File tiff = new File("temp.tif");

//        try {
//            tiff = PdfUtilities.convertPdf2Tiff(pdf);
//        } catch (IOException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
        BufferedImage image = null;
        try {
            String location="pdf-%d.%s";
          // generateImageFromPDF("/Users/durdan/devel/projects/edatablock-ocr/src/main/resources/test-data/INVOICE 2312874115.pdf","bmp",location);

           image = ImageIO.read(new FileInputStream(pdf));
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        ByteBuffer buf = ImageIOHelper.convertImageData(image);
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);

        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        TessAPI1.TessBaseAPISetPageSegMode(handle, ITessAPI.TessPageSegMode.PSM_AUTO);
        TessAPI1.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        System.out.println("Image Width "+image.getWidth()+" Image Height"+image.getHeight());
        ITessAPI.ETEXT_DESC monitor = new ITessAPI.ETEXT_DESC();
        TessAPI1.TessBaseAPIRecognize(handle, monitor);
        ITessAPI.TessResultIterator ri = TessAPI1.TessBaseAPIGetIterator(handle);
        ITessAPI.TessPageIterator pi = TessAPI1.TessResultIteratorGetPageIterator(ri);
        TessAPI1.TessPageIteratorBegin(pi);
        System.out.println("Bounding boxes:\nchar(s) left top right bottom confidence font-attributes");
        int level = ITessAPI.TessPageIteratorLevel.RIL_WORD;
        int height = image.getHeight();
        do {
            Pointer ptr = TessAPI1.TessResultIteratorGetUTF8Text(ri, level);
            String word = ptr.getString(0);
            TessAPI1.TessDeleteText(ptr);
            float confidence = TessAPI1.TessResultIteratorConfidence(ri, level);
            IntBuffer leftB = IntBuffer.allocate(1);
            IntBuffer topB = IntBuffer.allocate(1);
            IntBuffer rightB = IntBuffer.allocate(1);
            IntBuffer bottomB = IntBuffer.allocate(1);
            TessAPI1.TessPageIteratorBoundingBox(pi, level, leftB, topB, rightB, bottomB);
            //System.out.println(String.format("%d %d %d %d", leftB, topB, rightB, bottomB));
          //  System.out.println(String.format("%d %d %d %d", leftB, topB, rightB, bottomB));
            //TessAPI1.TessPageIteratorBoundingBox(pi, level, Integer.valueOf(1321),16, 439, 55);
           // AWE 1321 16 1439 55-----// AWB 1321(X1) 16(Y1) 1439(X2) 55(Y2)


            int left = leftB.get();
            int top = topB.get();
            int right = rightB.get();
            int bottom = bottomB.get();
            System.out.println(String.format("%s %d %d %d %d %f", word, left, top, right, bottom, confidence));
            IntBuffer boldB = IntBuffer.allocate(1);
            IntBuffer italicB = IntBuffer.allocate(1);
            IntBuffer underlinedB = IntBuffer.allocate(1);
            IntBuffer monospaceB = IntBuffer.allocate(1);
            IntBuffer serifB = IntBuffer.allocate(1);
            IntBuffer smallcapsB = IntBuffer.allocate(1);
            IntBuffer pointSizeB = IntBuffer.allocate(1);
            IntBuffer fontIdB = IntBuffer.allocate(1);
            String fontName = TessAPI1.TessResultIteratorWordFontAttributes(ri, boldB, italicB, underlinedB,
                    monospaceB, serifB, smallcapsB, pointSizeB, fontIdB);
            boolean bold = boldB.get() == TRUE;
            boolean italic = italicB.get() == TRUE;
            boolean underlined = underlinedB.get() == TRUE;
            boolean monospace = monospaceB.get() == TRUE;
            boolean serif = serifB.get() == TRUE;
            boolean smallcaps = smallcapsB.get() == TRUE;
            int pointSize = pointSizeB.get();
            int fontId = fontIdB.get();
//            System.out.println(String.format(" font: %s, size: %d, font id: %d, bold: %b,"
//                            + " italic: %b, underlined: %b, monospace: %b, serif: %b, smallcap: %b",
//                    fontName, pointSize, fontId, bold, italic, underlined, monospace, serif, smallcaps));
        } while (TessAPI1.TessPageIteratorNext(pi, level) == TRUE);


    }

    private static void generateImageFromPDF(String filename, String extension,String location) throws IOException {
        PDDocument document = PDDocument.load(new File(filename));
        System.out.println("Test123445666666666"+document.getDocumentInformation().getTitle());
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        for (int page = 0; page < document.getNumberOfPages(); ++page) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(
                    page, 300, ImageType.RGB);
            System.out.println("Test@@@@@@@@@@@@@@@@@@@@@@"+String.format(location, page + 1, extension));
          ImageIOUtil.writeImage(
                    bim, String.format(location, page + 1, extension), 300);
        }
        document.close();
    }

    public static void testOSD() throws Exception {
        String datapath = "/usr/local/Cellar/tesseract/3.05.01/share/";
        String language = "eng";
        ITessAPI.TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        logger.info("OSD");
        int expResult = ITessAPI.TessPageSegMode.PSM_AUTO_OSD;
        IntBuffer orientation = IntBuffer.allocate(1);
        IntBuffer direction = IntBuffer.allocate(1);
        IntBuffer order = IntBuffer.allocate(1);
        FloatBuffer deskew_angle = FloatBuffer.allocate(1);
        File pdf = new File("/Users/durdan/devel/projects/edatablock-ocr/src/main/resources/test-data/AWB 2312874115.pdf");
        File tiff = new File("pdf-1.tiff");

        try {
            tiff = PdfUtilities.convertPdf2Tiff(pdf);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        BufferedImage image = null;
        try {
            String location="pdf-%d.%s";
            generateImageFromPDF("/Users/durdan/devel/projects/edatablock-ocr/src/main/resources/test-data/INVOICE 2312874115.pdf","tiff",location);

            image = ImageIO.read(new FileInputStream(tiff));
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
         image = ImageIO.read(new FileInputStream(tiff)); // require jai-imageio lib to read TIFF
        ByteBuffer buf = ImageIOHelper.convertImageData(image);
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        TessAPI1.TessBaseAPISetPageSegMode(handle, ITessAPI.TessPageSegMode.PSM_AUTO_OSD);
        int actualResult = TessAPI1.TessBaseAPIGetPageSegMode(handle);
        logger.info("PSM: " + Utils.getConstantName(actualResult, ITessAPI.TessPageSegMode.class));
        TessAPI1.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        int success = TessAPI1.TessBaseAPIRecognize(handle, null);
        if (success == 0) {
            ITessAPI.TessPageIterator pi = TessAPI1.TessBaseAPIAnalyseLayout(handle);
            TessAPI1.TessPageIteratorOrientation(pi, orientation, direction, order, deskew_angle);
            logger.info(String.format(
                    "Orientation: %s\nWritingDirection: %s\nTextlineOrder: %s\nDeskew angle: %.4f\n",
                    Utils.getConstantName(orientation.get(), ITessAPI.TessOrientation.class),
                    Utils.getConstantName(direction.get(), ITessAPI.TessWritingDirection.class),
                    Utils.getConstantName(order.get(), ITessAPI.TessTextlineOrder.class),
                    deskew_angle.get()));
        }
        System.out.println("Resukt&&&&&&&"  +expResult+ actualResult);
    }

    // SetGrayscale
//    private Bitmap setGrayscale(Bitmap img) {
//        Bitmap bmap = img.copy(img.getConfig(), true);
//        int c;
//        for (int i = 0; i < bmap.getWidth(); i++) {
//            for (int j = 0; j < bmap.getHeight(); j++) {
//                c = bmap.getPixel(i, j);
//                byte gray = (byte) (.299 * Color.red(c) + .587 * Color.green(c)
//                        + .114 * Color.blue(c));
//
//                bmap.setPixel(i, j, Color.argb(255, gray, gray, gray));
//            }
//        }
//        return bmap;
//    }
//
//    // RemoveNoise
//    private Bitmap removeNoise(Bitmap bmap) {
//        for (int x = 0; x < bmap.getWidth(); x++) {
//            for (int y = 0; y < bmap.getHeight(); y++) {
//                int pixel = bmap.getPixel(x, y);
//                if (Color.red(pixel) < 162 && Color.green(pixel) < 162 && Color.blue(pixel) < 162) {
//                    bmap.setPixel(x, y, Color.BLACK);
//                }
//            }
//        }
//        for (int x = 0; x < bmap.getWidth(); x++) {
//            for (int y = 0; y < bmap.getHeight(); y++) {
//                int pixel = bmap.getPixel(x, y);
//                if (Color.red(pixel) > 162 && Color.green(pixel) > 162 && Color.blue(pixel) > 162) {
//                    bmap.setPixel(x, y, Color.WHITE);
//                }
//            }
//        }
//        return bmap;
//    }

    public static void getWordWithZone(){
        File imageFile = new File("/Users/durdan/devel/projects/edatablock-ocr/src/main/resources/test-data/1.png");
        /**
         * JNA Interface Mapping
         **/

        String TESS4J_FOLDER_PATH="/usr/local/Cellar/tesseract/3.05.01/share/";

        ITesseract instance = new Tesseract();
        ///



        ////////




        /**
         * You either set your own tessdata folder with your custom language pack or
         * use LoadLibs to load the default tessdata folder for you.
         **/
        instance.setDatapath(TESS4J_FOLDER_PATH);
        //In case you don't have your own tessdata, let it also be extracted for you
        //File tessDataFolder = LoadLibs.extractTessResources(TESS4J_FOLDER_PATH);

        //Set the tessdata path
        //instance.setDatapath(tessDataFolder.getAbsolutePath());
        instance.setLanguage("eng");

        // instance.setDatapath(LoadLibs.extractTessResources("tessdata").getParent());
//        instance.//

        try {
            //System.out.println(tessDataFolder.getAbsolutePath());
            Rectangle rect = new Rectangle(1315, 0, 895, 74);
            Rectangle rect1 = new Rectangle(136, 154, 491, 274);
            Rectangle rect2 = new Rectangle(143, 620, 894, 540);
            //Rectangle rect3 = new Rectangle(1321, 16, (2263-1321), 55-16);

            String result = instance.doOCR(imageFile,rect);
            String result2 = instance.doOCR(imageFile,rect1);
            String result3 = instance.doOCR(imageFile,rect2);

            System.out.println("Zone based Result*****"+result);
            System.out.println("Zone based Result*****"+result2);
            System.out.println("Zone based Result*****"+result3);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }


    /////////////////////

    /**
     * Test of TessBaseAPIAnalyseLayout method, of class TessAPI1.
     *
     * @throws java.lang.Exception
     */

    public static void getBaseAPIAnalyseLayout() throws Exception {
        logger.info("TessBaseAPIAnalyseLayout");
       // File image = new File(testResourcesDataPath, "eurotext.png");
        int expResult = 12; // number of lines in the test image
        Leptonica leptInstance = Leptonica.INSTANCE;
        File tiff = new File("pdf-1.tiff");
        try {
            tiff = PdfUtilities.convertPdf2Tiff(imageFile);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String datapath = "/usr/local/Cellar/tesseract/3.05.01/share/";
        String language = "eng";
        ITessAPI.TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        Pix pix = leptInstance.pixRead(tiff.getPath());
        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        TessAPI1.TessBaseAPISetImage2(handle, pix);
        int pageIteratorLevel = ITessAPI.TessPageIteratorLevel.RIL_WORD;
        logger.info("PageIteratorLevel: " + Utils.getConstantName(pageIteratorLevel, ITessAPI.TessPageIteratorLevel.class));
        int i = 0;
        ITessAPI.TessPageIterator pi = TessAPI1.TessBaseAPIAnalyseLayout(handle);
        //ITessAPI.TessResultIterator ri = TessAPI1.TessBaseAPIGetIterator(handle);


        do {

            IntBuffer leftB = IntBuffer.allocate(1);
            IntBuffer topB = IntBuffer.allocate(1);
            IntBuffer rightB = IntBuffer.allocate(1);
            IntBuffer bottomB = IntBuffer.allocate(1);
            TessAPI1.TessPageIteratorBoundingBox(pi, pageIteratorLevel, leftB, topB, rightB, bottomB);

            int left = leftB.get();
            int top = topB.get();
            int right = rightB.get();
            int bottom = bottomB.get();

            logger.info(String.format(" Box[%d]: x=%d, y=%d, w=%d, h=%d", i++, left, top, right - left, bottom - top));
        } while (TessAPI1.TessPageIteratorNext(pi, pageIteratorLevel) == TRUE);
        TessAPI1.TessPageIteratorDelete(pi);
        //assertEquals(expResult, i);
    }


    ///////////////////

}
