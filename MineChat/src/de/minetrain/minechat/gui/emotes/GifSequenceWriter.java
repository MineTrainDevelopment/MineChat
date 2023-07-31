package de.minetrain.minechat.gui.emotes;

import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

public class GifSequenceWriter {
	protected ImageWriter gifWriter;
    protected ImageWriteParam imageWriteParam;
    protected IIOMetadata imageMetaData;

    /**
     * Creates a new GifSequenceWriter
     * 
     * @param outputStream the ImageOutputStream to be written to
     * @param imageType    one of the imageTypes specified in BufferedImage
     * @param timePerFrame the delay between each frame in hundredths of a second
     * @param loop         whether to loop the animation endlessly
     * @throws IOException if an I/O exception occurs
     */
    public GifSequenceWriter(ImageOutputStream outputStream, int imageType, int timePerFrame, boolean loop)
            throws IOException {
        gifWriter = ImageIO.getImageWritersBySuffix("gif").next();
        imageWriteParam = gifWriter.getDefaultWriteParam();

        ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType);

        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_gif_writer_1.0");
        IIOMetadataNode graphicControlExtension = new IIOMetadataNode("GraphicControlExtension");
        graphicControlExtension.setAttribute("disposalMethod", "none");
        graphicControlExtension.setAttribute("userInputFlag", "FALSE");
        graphicControlExtension.setAttribute("transparentColorFlag", "FALSE");
        graphicControlExtension.setAttribute("delayTime", Integer.toString(timePerFrame));
        graphicControlExtension.setAttribute("transparentColorIndex", "0");
        IIOMetadataNode comments = new IIOMetadataNode("CommentExtensions");
        comments.setAttribute("CommentExtension", "Created by GifSequenceWriter");
        IIOMetadataNode appExtensions = new IIOMetadataNode("ApplicationExtensions");
        IIOMetadataNode appExtension = new IIOMetadataNode("ApplicationExtension");
        appExtension.setAttribute("applicationID", "NETSCAPE");
        appExtension.setAttribute("authenticationCode", "2.0");
        byte[] loopBytes = new byte[] { 0x1, (byte) (loop ? 0x0 : 0x1), 0x0 };
        appExtension.setUserObject(loopBytes);
        appExtensions.appendChild(appExtension);

        root.appendChild(graphicControlExtension);
        root.appendChild(comments);
        root.appendChild(appExtensions);

        imageMetaData = gifWriter.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);
        String metaFormatName = imageMetaData.getNativeMetadataFormatName();
        IIOMetadataNode metaRoot = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);
        metaRoot.appendChild(root);
        imageMetaData.setFromTree(metaFormatName, metaRoot);

        gifWriter.setOutput(outputStream);
        gifWriter.prepareWriteSequence(null);
    }
    
    /**
     * Writes the next frame in the animation sequence
     * 
     * @param img the BufferedImage to be added as the next frame in the sequence
     * @throws IOException if an I/O exception occurs
     */
    public void writeToSequence(RenderedImage img) throws IOException {
    	gifWriter.writeToSequence(new IIOImage(img, null, imageMetaData), imageWriteParam);
    }
    
    /**
     * Closes this GifSequenceWriter object. This does not close the underlying stream.
     * 
     * @throws IOException if an I/O exception occurs
     */
    public void close() throws IOException {
        gifWriter.endWriteSequence();
    }

    /**
     * Returns a new IndexColorModel with alpha channel
     * 
     * @param bits the number of bits in the color index
     * @return a new IndexColorModel with alpha channel
     */
    public static IndexColorModel createColorModel(int bits) {
        int numColors = 1 << bits;
        byte[] reds = new byte[numColors];
        byte[] greens = new byte[numColors];
        byte[] blues = new byte[numColors];
        byte[] alphas = new byte[numColors];

        // set colors from 0 to 254 to be gray
        for (int i = 0; i < 255; i++) {
            reds[i] = (byte) i;
            greens[i] = (byte) i;
            blues[i] = (byte) i;
            alphas[i] = (byte) 255;
        }

        // set color 255 to be transparent
        alphas[255] = (byte) 0;

        return new IndexColorModel(bits, numColors, reds, greens, blues, alphas);
    }
}
