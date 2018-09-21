package com.edatablock.ocr;

import com.sun.jna.Pointer;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TessAPI1;
import net.sourceforge.tess4j.util.ImageHelper;
import net.sourceforge.tess4j.util.ImageIOHelper;
import net.sourceforge.tess4j.util.LoggHelper;
import net.sourceforge.tess4j.util.PdfUtilities;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static net.sourceforge.tess4j.ITessAPI.TRUE;

public class GetWordsWithZone {


    private static final Logger logger = LoggerFactory.getLogger(new LoggHelper().toString());
    static final double MINIMUM_DESKEW_THRESHOLD = 0.05d;
    String TESS4J_FOLDER_PATH="/usr/local/Cellar/tesseract/3.05.01/share/";
    ITesseract instance;

    private final String datapath = "/Users/durdan/devel/projects/edatablock-ocr/src/main/resources/tessdata";
    private final String testResourcesDataPath = "/Users/durdan/devel/projects/edatablock-ocr/src/main/resources/test-data/INVOICE 2312874115.pdf";

    public void testGetWords() throws Exception {
        logger.info("getWords");
        String datapath = "/usr/local/Cellar/tesseract/3.05.01/share/";
        String language = "eng";
        ITessAPI.TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        //////
        //load PostScript document


        //System.setProperty(PdfUtilities.PDF_LIBRARY, PdfUtilities.PDFBOX);
        //////

        File pdf = new File("/Users/durdan/devel/projects/edatablock-ocr/src/main/resources/test-data/INVOICE 2312874115.pdf");
        File tiff = new File("/Users/durdan/devel/projects/edatablock-ocr/src/main/resources/test-data/temp.tif");


        try {
            tiff = PdfUtilities.convertPdf2Tiff(pdf);

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        BufferedImage image = null;
        try {
            String location="pdf-%d.%s";
            // generateImageFromPDF("/Users/durdan/devel/projects/edatablock-ocr/src/main/resources/test-data/INVOICE 2312874115.pdf","bmp",location);

            image = ImageIO.read(new FileInputStream(tiff));
            image=ImageHelper.convertImageToGrayscale(image);



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


    public void PdfToImage(String PDFFILE){
        try{

            PDDocument document = PDDocument.load(new File(PDFFILE));
            PDPage pd;

            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); ++page)
            {


                pd = document.getPage(page);
                //pd.setCropBox(new PDRectangle(100, 100,100,100));
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                ImageIOUtil.writeImage(bim, "/Users/durdan/devel/projects/edatablock-ocr/target/"+ "test" + ".png", 300);
                logger.info("File name is created ",(page+1)+".png");
            }
            document.close();
        }catch (Exception ex){
            logger.info("Exception",ex);
        }
    }

}
