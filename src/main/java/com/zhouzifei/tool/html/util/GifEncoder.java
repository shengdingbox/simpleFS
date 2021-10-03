package com.zhouzifei.tool.html.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * AnimatedGifEncoder类-对由一个或多个帧组成的GIF文件进行编码。 *
 * <pre> *示例：*
 * AnimatedGifEncoder e = new AnimatedGifEncoder（）;
 * * e.start（outputFileName）;
 * * e.setDelay（1000）; //每秒1帧
 * * e.addFrame（image1）;
 * * e.addFrame（image2）; * e.finish（）;
 * * </ pre> *此类源代码未声明任何版权。可以出于任何目的使用
 * *，但是，请参见Unisys LZW专利以获取有关使用相关的Encoder类的限制
 * *。请将所有更正*转发至fmsware.com上的问题。
 *
 * @author 周子斐 (17600004572@163.com)
 * @version 1.03 November 2003
 */
public class GifEncoder {
    protected int width; // image size
    protected int height;
    protected Color transparent = null; // transparent color if given
    protected int transIndex; // transparent index in color table
    protected int repeat = -1; // no repeat
    protected int delay = 0; // frame delay (hundredths)
    protected boolean started = false; // ready to output frames
    protected OutputStream out;
    protected BufferedImage image; // current frame
    protected byte[] pixels; // BGR byte array from frame
    protected byte[] indexedPixels; // converted frame indexed to palette
    protected int colorDepth; // number of bit planes
    protected byte[] colorTab; // RGB palette
    protected boolean[] usedEntry = new boolean[256]; // active palette entries
    protected int palSize = 7; // color table size (bits-1)
    protected int dispose = -1; // disposal code (-1 = use default)
    protected boolean closeStream = false; // close stream when finished
    protected boolean firstFrame = true;
    protected boolean sizeSet = false; // if false, get size from first frame
    protected int sample = 10; // default sample interval for quantizer

    /**
     * 设置每帧之间的延迟时间，或将其更改为*以用于后续帧（适用于添加的最后一帧）。
     *
     * @param ms
     *         int delay time in milliseconds
     */
    public void setDelay(int ms) {
        delay = Math.round(ms / 10.0f);
    }

    /**
     * 设置最后添加的帧*和任何后续帧的GIF帧处理代码。如果未设置透明*颜色，则默认值为0，否则为2。
     *
     * @param code
     *         int disposal code.
     */
    public void setDispose(int code) {
        if (code >= 0) {
            dispose = code;
        }
    }

    /**
     * 设置应播放一组GIF帧*的次数。默认为1； 0表示无限期播放*。必须在添加第一个*图像之前调用。
     *
     * @param iter
     *         int number of iterations.
     * @return
     */
    public void setRepeat(int iter) {
        if (iter >= 0) {
            repeat = iter;
        }
    }

    /**
     * 设置最后添加的帧*和任何后续帧的透明颜色。
     * 由于所有颜色都可能在量化过程中进行修改*，
     * 因此最终*调色板中最接近给定颜色*的每个帧的颜色变为该帧的透明颜色。
     * 可以设置为null表示没有透明颜色。
     *
     * @param c
     *         Color to be treated as transparent on display.
     */
    public void setTransparent(Color c) {
        transparent = c;
    }

    /**
     * 添加下一个GIF帧。不会立即写入该帧，但实际上会延迟
     * 直到接收到下一帧，以便可以插入定时*数据。调用
     * <code> finish（）</code>会刷新所有*帧。
     * 如果未调用<code> setSize </code>，则第一张图像的大小将用于所有后续帧。
     *
     * @param im
     *         BufferedImage containing frame to write.
     * @return true if successful.
     */
    public boolean addFrame(BufferedImage im) {
        if ((im == null) || !started) {
            return false;
        }
        boolean ok = true;
        try {
            if (!sizeSet) {
                // use first frame's size
                setSize(im.getWidth(), im.getHeight());
            }
            image = im;
            getImagePixels(); // convert to correct format if necessary
            analyzePixels(); // build color table & map pixels
            if (firstFrame) {
                writeLSD(); // logical screen descriptior
                writePalette(); // global color table
                if (repeat >= 0) {
                    // use NS app extension to indicate reps
                    writeNetscapeExt();
                }
            }
            writeGraphicCtrlExt(); // write graphic control extension
            writeImageDesc(); // image descriptor
            if (!firstFrame) {
                writePalette(); // local color table
            }
            writePixels(); // encode and write pixel data
            firstFrame = false;
        } catch (IOException e) {
            ok = false;
        }

        return ok;
    }

    //added by alvaro
    public boolean outFlush() {
        boolean ok = true;
        try {
            out.flush();
            return ok;
        } catch (IOException e) {
            ok = false;
        }

        return ok;
    }

    public byte[] getFrameByteArray() {
        return ((ByteArrayOutputStream) out).toByteArray();
    }

    /**
     * 刷新所有未决数据并关闭输出文件。 *如果写入OutputStream，则不会关闭流。
     */
    public boolean finish() {
        if (!started) return false;
        boolean ok = true;
        started = false;
        try {
            out.write(0x3b); // gif trailer
            out.flush();
            if (closeStream) {
                out.close();
            }
        } catch (IOException e) {
            ok = false;
        }

        return ok;
    }

    public void reset() {
        // reset for subsequent use
        transIndex = 0;
        out = null;
        image = null;
        pixels = null;
        indexedPixels = null;
        colorTab = null;
        closeStream = false;
        firstFrame = true;
    }

    /**
     * Sets frame rate in frames per second.  Equivalent to
     * <code>setDelay(1000/fps)</code>.
     *
     * @param fps
     *         float frame rate (frames per second)
     */
    public void setFrameRate(float fps) {
        if (fps != 0f) {
            delay = Math.round(100f / fps);
        }
    }

    /**
     * 设置颜色量化的质量（将图像*转换为GIF规范允许的最大256种颜色）。
     * 较低的值（最小值= 1）产生更好的颜色，但处理速度明显变慢。默认值为10，并以合理的速度产生*良好的色彩映射。
     * 值大于20不会显着提高速度。
     *
     * @param quality
     *         int greater than 0.
     * @return
     */
    public void setQuality(int quality) {
        if (quality < 1) {
            quality = 1;
        }
        sample = quality;
    }

    /**
     * Sets the GIF frame size.  The default size is the
     * size of the first frame added if this method is
     * not invoked.
     *
     * @param w
     *         int frame width.
     * @param h
     *         int frame width.
     */
    public void setSize(int w, int h) {
        if (started && !firstFrame) return;
        width = w;
        height = h;
        if (width < 1) width = 320;
        if (height < 1) height = 240;
        sizeSet = true;
    }

    /**
     * Initiates GIF file creation on the given stream.  The stream
     * is not closed automatically.
     *
     * @param os
     *         OutputStream on which GIF images are written.
     * @return false if initial write failed.
     */
    public boolean start(OutputStream os) {
        if (os == null) return false;
        boolean ok = true;
        closeStream = false;
        out = os;
        try {
            writeString("GIF89a"); // header
        } catch (IOException e) {
            ok = false;
        }
        return started = ok;
    }

    /**
     * Initiates writing of a GIF file with the specified name.
     *
     * @param file
     *         String containing output file name.
     * @return false if open or initial write failed.
     */
    public boolean start(String file) {
        boolean ok = true;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            ok = start(out);
            closeStream = true;
        } catch (IOException e) {
            ok = false;
        }
        return started = ok;
    }

    /**
     * Analyzes image colors and creates color map.
     */
    protected void analyzePixels() {
        int len = pixels.length;
        int nPix = len / 3;
        indexedPixels = new byte[nPix];
        Quant nq = new Quant(pixels, len, sample);
        // initialize quantizer
        colorTab = nq.process(); // create reduced palette
        // convert map from BGR to RGB
        for (int i = 0; i < colorTab.length; i += 3) {
            byte temp = colorTab[i];
            colorTab[i] = colorTab[i + 2];
            colorTab[i + 2] = temp;
            usedEntry[i / 3] = false;
        }
        // map image pixels to new palette
        int k = 0;
        for (int i = 0; i < nPix; i++) {
            int index =
                    nq.map(pixels[k++] & 0xff,
                            pixels[k++] & 0xff,
                            pixels[k++] & 0xff);
            usedEntry[index] = true;
            indexedPixels[i] = (byte) index;
        }
        pixels = null;
        colorDepth = 8;
        palSize = 7;
        // get closest match to transparent color if specified
        if (transparent != null) {
            transIndex = findClosest(transparent);
        }
    }

    /**
     * Returns index of palette color closest to c
     */
    protected int findClosest(Color c) {
        if (colorTab == null) return -1;
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int minpos = 0;
        int dmin = 256 * 256 * 256;
        int len = colorTab.length;
        for (int i = 0; i < len; ) {
            int dr = r - (colorTab[i++] & 0xff);
            int dg = g - (colorTab[i++] & 0xff);
            int db = b - (colorTab[i] & 0xff);
            int d = dr * dr + dg * dg + db * db;
            int index = i / 3;
            if (usedEntry[index] && (d < dmin)) {
                dmin = d;
                minpos = index;
            }
            i++;
        }
        return minpos;
    }

    /**
     * Extracts image pixels into byte array "pixels"
     */
    protected void getImagePixels() {
        int w = image.getWidth();
        int h = image.getHeight();
        int type = image.getType();
        if ((w != width)
                || (h != height)
                || (type != BufferedImage.TYPE_3BYTE_BGR)) {
            // create new image with right size/format
            BufferedImage temp =
                    new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g = temp.createGraphics();
            g.drawImage(image, 0, 0, null);
            image = temp;
        }
        pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    }

    /**
     * Writes Graphic Control Extension
     */
    protected void writeGraphicCtrlExt() throws IOException {
        out.write(0x21); // extension introducer
        out.write(0xf9); // GCE label
        out.write(4); // data block size
        int transp, disp;
        if (transparent == null) {
            transp = 0;
            disp = 0; // dispose = no action
        } else {
            transp = 1;
            disp = 2; // force clear if using transparent color
        }
        if (dispose >= 0) {
            disp = dispose & 7; // user override
        }
        disp <<= 2;

        // packed fields
        out.write(0 | // 1:3 reserved
                disp | // 4:6 disposal
                0 | // 7   user input - 0 = none
                transp); // 8   transparency flag

        writeShort(delay); // delay x 1/100 sec
        out.write(transIndex); // transparent color index
        out.write(0); // block terminator
    }

    /**
     * Writes Image Descriptor
     */
    protected void writeImageDesc() throws IOException {
        out.write(0x2c); // image separator
        writeShort(0); // image position x,y = 0,0
        writeShort(0);
        writeShort(width); // image size
        writeShort(height);
        // packed fields
        if (firstFrame) {
            // no LCT  - GCT is used for first (or only) frame
            out.write(0);
        } else {
            // specify normal LCT
            out.write(0x80 | // 1 local color table  1=yes
                    0 | // 2 interlace - 0=no
                    0 | // 3 sorted - 0=no
                    0 | // 4-5 reserved
                    palSize); // 6-8 size of color table
        }
    }

    /**
     * Writes Logical Screen Descriptor
     */
    protected void writeLSD() throws IOException {
        // logical screen size
        writeShort(width);
        writeShort(height);
        // packed fields
        out.write((0x80 | // 1   : global color table flag = 1 (gct used)
                0x70 | // 2-4 : color resolution = 7
                0x00 | // 5   : gct sort flag = 0
                palSize)); // 6-8 : gct size

        out.write(0); // background color index
        out.write(0); // pixel aspect ratio - assume 1:1
    }

    /**
     * Writes Netscape application extension to define
     * repeat count.
     */
    protected void writeNetscapeExt() throws IOException {
        out.write(0x21); // extension introducer
        out.write(0xff); // app extension label
        out.write(11); // block size
        writeString("NETSCAPE" + "2.0"); // app id + auth code
        out.write(3); // sub-block size
        out.write(1); // loop sub-block id
        writeShort(repeat); // loop count (extra iterations, 0=repeat forever)
        out.write(0); // block terminator
    }

    /**
     * Writes color table
     */
    protected void writePalette() throws IOException {
        out.write(colorTab, 0, colorTab.length);
        int n = (3 * 256) - colorTab.length;
        for (int i = 0; i < n; i++) {
            out.write(0);
        }
    }

    /**
     * Encodes and writes pixel data
     */
    protected void writePixels() throws IOException {
        Encoder encoder = new Encoder(width, height, indexedPixels, colorDepth);
        encoder.encode(out);
    }

    /**
     * Write 16-bit value to output stream, LSB first
     */
    protected void writeShort(int value) throws IOException {
        out.write(value & 0xff);
        out.write((value >> 8) & 0xff);
    }

    /**
     * Writes string to output stream
     */
    protected void writeString(String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            out.write((byte) s.charAt(i));
        }
    }
}
