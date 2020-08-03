/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package gervill.javax.sound.sampled;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import impl.SourceDataLineImpl;
import gervill.javax.sound.sampled.spi.AudioFileReader;
import gervill.javax.sound.sampled.spi.FormatConversionProvider;
import gervill.javax.sound.sampled.spi.MixerProvider;

import gervill.com.sun.media.sound.JDK13Services;

/* $fb TODO:
 * - consistent usage of (typed) collections
 */


/**
 * The <code>AudioSystem</code> class acts as the entry point to the
 * sampled-audio system resources. This class lets you query and
 * access the mixers that are installed on the system.
 * <code>AudioSystem</code> includes a number of
 * methods for converting audio data between different formats, and for
 * translating between audio files and streams. It also provides a method
 * for obtaining a <code> Line</code> directly from the
 * <code>AudioSystem</code> without dealing explicitly
 * with mixers.
 *
 * <p>Properties can be used to specify the default mixer
 * for specific line types.
 * Both system properties and a properties file are considered.
 * The <code>sound.properties</code> properties file is read from
 * an implementation-specific location (typically it is the <code>lib</code>
 * directory in the Java installation directory).
 * If a property exists both as a system property and in the
 * properties file, the system property takes precedence. If none is
 * specified, a suitable default is chosen among the available devices.
 * The syntax of the properties file is specified in
 *  java.util.Properties#load(InputStream) Properties.load. The
 * following table lists the available property keys and which methods
 * consider them:
 *
 * <table border=0>
 *  <caption>Audio System Property Keys</caption>
 *  <tr>
 *   <th>Property Key</th>
 *   <th>Interface</th>
 *   <th>Affected Method(s)</th>
 *  </tr>
 *  <tr>
 *   <td><code>gervill.javax.sound.sampled.Clip</code></td>
 *   <td> Clip</td>
 *   <td> #getLine,  #getClip</td>
 *  </tr>
 *  <tr>
 *   <td><code>gervill.javax.sound.sampled.Port</code></td>
 *   <td> Port</td>
 *   <td> #getLine</td>
 *  </tr>
 *  <tr>
 *   <td><code>gervill.javax.sound.sampled.SourceDataLine</code></td>
 *   <td> SourceDataLine</td>
 *   <td> #getLine,  #getSourceDataLine</td>
 *  </tr>
 *  <tr>
 *   <td><code>gervill.javax.sound.sampled.TargetDataLine</code></td>
 *   <td> TargetDataLine</td>
 *   <td> #getLine,  #getTargetDataLine</td>
 *  </tr>
 * </table>
 *
 * The property value consists of the provider class name
 * and the mixer name, separated by the hash mark (&quot;#&quot;).
 * The provider class name is the fully-qualified
 * name of a concrete  gervill.javax.sound.sampled.spi.MixerProvider
 * mixer provider class. The mixer name is matched against
 * the <code>String</code> returned by the <code>getName</code>
 * method of <code>Mixer.Info</code>.
 * Either the class name, or the mixer name may be omitted.
 * If only the class name is specified, the trailing hash mark
 * is optional.
 *
 * <p>If the provider class is specified, and it can be
 * successfully retrieved from the installed providers, the list of
 * <code>Mixer.Info</code> objects is retrieved
 * from the provider. Otherwise, or when these mixers
 * do not provide a subsequent match, the list is retrieved
 * from  #getMixerInfo to contain
 * all available <code>Mixer.Info</code> objects.
 *
 * <p>If a mixer name is specified, the resulting list of
 * <code>Mixer.Info</code> objects is searched:
 * the first one with a matching name, and whose
 * <code>Mixer</code> provides the
 * respective line interface, will be returned.
 * If no matching <code>Mixer.Info</code> object
 * is found, or the mixer name is not specified,
 * the first mixer from the resulting
 * list, which provides the respective line
 * interface, will be returned.
 *
 * For example, the property <code>gervill.javax.sound.sampled.Clip</code>
 * with a value
 * <code>&quot;gervill.com.sun.media.sound.MixerProvider#SunClip&quot;</code>
 * will have the following consequences when
 * <code>getLine</code> is called requesting a <code>Clip</code>
 * instance:
 * if the class <code>gervill.com.sun.media.sound.MixerProvider</code> exists
 * in the list of installed mixer providers,
 * the first <code>Clip</code> from the first mixer with name
 * <code>&quot;SunClip&quot;</code> will be returned. If it cannot
 * be found, the first <code>Clip</code> from the first mixer
 * of the specified provider will be returned, regardless of name.
 * If there is none, the first <code>Clip</code> from the first
 * <code>Mixer</code> with name
 * <code>&quot;SunClip&quot;</code> in the list of all mixers
 * (as returned by <code>getMixerInfo</code>) will be returned,
 * or, if not found, the first <code>Clip</code> of the first
 * <code>Mixer</code>that can be found in the list of all
 * mixers is returned.
 * If that fails, too, an <code>IllegalArgumentException</code>
 * is thrown.
 *
 * @author Kara Kytle
 * @author Florian Bomers
 * @author Matthias Pfisterer
 * @author Kevin P. Smith
 *
 * see AudioFormat
 * see AudioInputStream
 * see Mixer
 * see Line
 * see Line.Info
 * @since 1.3
 */
public class AudioSystem {

    /**
     * An integer that stands for an unknown numeric value.
     * This value is appropriate only for signed quantities that do not
     * normally take negative values.  Examples include file sizes, frame
     * sizes, buffer sizes, and sample rates.
     * A number of Java Sound constructors accept
     * a value of <code>NOT_SPECIFIED</code> for such parameters.  Other
     * methods may also accept or return this value, as documented.
     */
    public static final int NOT_SPECIFIED = -1;

    /**
     * Private no-args constructor for ensuring against instantiation.
     */
    private AudioSystem() {
    }


    /**
     * Obtains the requested audio mixer.
     * @param info a <code>Mixer.Info</code> object representing the desired
     * mixer, or <code>null</code> for the system default mixer
     * @return the requested mixer
     * throws SecurityException if the requested mixer
     * is unavailable because of security restrictions
     * throws IllegalArgumentException if the info object does not represent
     * a mixer installed on the system
     * see #getMixerInfo
     */
    public static Mixer getMixer(Mixer.Info info) {

        List providers = getMixerProviders();

        for(int i = 0; i < providers.size(); i++ ) {

            try {
                return ((MixerProvider)providers.get(i)).getMixer(info);

            } catch (IllegalArgumentException e) {
            } catch (NullPointerException e) {
                // $$jb 08.20.99:  If the strings in the info object aren't
                // set, then Netscape (using jdk1.1.5) tends to throw
                // NPE's when doing some string manipulation.  This is
                // probably not the best fix, but is solves the problem
                // of the NPE in Netscape using local classes
                // $$jb 11.01.99: Replacing this patch.
            }
        }

        //$$fb if looking for default mixer, and not found yet, add a round of looking
        if (info == null) {
            for(int i = 0; i < providers.size(); i++ ) {
                try {
                    MixerProvider provider = (MixerProvider) providers.get(i);
                    Mixer.Info[] infos = provider.getMixerInfo();
                    // start from 0 to last device (do not reverse this order)
                    for (int ii = 0; ii < infos.length; ii++) {
                        try {
                            return provider.getMixer(infos[ii]);
                        } catch (IllegalArgumentException e) {
                            // this is not a good default device :)
                        }
                    }
                } catch (IllegalArgumentException e) {
                } catch (NullPointerException e) {
                }
            }
        }


        throw new IllegalArgumentException("Mixer not supported: "
                                           + (info!=null?info.toString():"null"));
    }


    


    


    

    /**
     * Obtains a source data line that can be used for playing back
     * audio data in the format specified by the
     * <code>AudioFormat</code> object. The returned line
     * will be provided by the default system mixer, or,
     * if not possible, by any other mixer installed in the
     * system that supports a matching
     * <code>SourceDataLine</code> object.
     *
     * <p>The returned line should be opened with the
     * <code>open(AudioFormat)</code> or
     * <code>open(AudioFormat, int)</code> method.
     *
     * <p>This is a high-level method that uses <code>getMixer</code>
     * and <code>getLine</code> internally.
     *
     * <p>The returned <code>SourceDataLine</code>'s default
     * audio format will be initialized with <code>format</code>.
     *
     * <p>If the system property
     * <code>gervill.javax.sound.sampled.SourceDataLine</code>
     * is defined or it is defined in the file &quot;sound.properties&quot;,
     * it is used to retrieve the default source data line.
     * For details, refer to the  AudioSystem class description.
     *
     * @param format an <code>AudioFormat</code> object specifying
     *        the supported audio format of the returned line,
     *        or <code>null</code> for any audio format
     * @return the desired <code>SourceDataLine</code> object
     *
     * throws LineUnavailableException if a matching source data line
     *         is not available due to resource restrictions
     * throws SecurityException if a matching source data line
     *         is not available due to security restrictions
     * throws IllegalArgumentException if the system does not
     *         support at least one source data line supporting the
     *         specified audio format through any installed mixer
     *
     * see #getSourceDataLine(AudioFormat, Mixer.Info)
     * @since 1.5
     */
    public static SourceDataLine getSourceDataLine(AudioFormat format)
        throws LineUnavailableException{
        return new SourceDataLineImpl(format);
    }


    


    



    


    



    


    


    


    /**
     * Obtains the formats that have a particular encoding and that the system can
     * obtain from a stream of the specified format using the set of
     * installed format converters.
     * @param targetEncoding the desired encoding after conversion
     * @param sourceFormat the audio format before conversion
     * @return array of formats.  If no formats of the specified
     * encoding are supported, an array of length 0 is returned.
     */
    public static AudioFormat[] getTargetFormats(AudioFormat.Encoding targetEncoding, AudioFormat sourceFormat) {

        List codecs = getFormatConversionProviders();
        Vector formats = new Vector();

        int size = 0;
        int index = 0;
        AudioFormat fmts[] = null;

        // gather from all the codecs

        for(int i=0; i<codecs.size(); i++ ) {
            FormatConversionProvider codec = (FormatConversionProvider) codecs.get(i);
            fmts = codec.getTargetFormats(targetEncoding, sourceFormat);
            size += fmts.length;
            formats.addElement( fmts );
        }

        // now build a new array

        AudioFormat fmts2[] = new AudioFormat[size];
        for(int i=0; i<formats.size(); i++ ) {
            fmts = (AudioFormat [])(formats.get(i));
            for(int j=0; j<fmts.length; j++ ) {
                fmts2[index++] = fmts[j];
            }
        }
        return fmts2;
    }


    


    /**
     * Obtains an audio input stream of the indicated format, by converting the
     * provided audio input stream.
     * @param targetFormat the desired audio format after conversion
     * @param sourceStream the stream to be converted
     * @return an audio input stream of the indicated format
     * throws IllegalArgumentException if the conversion is not supported
     * #see #getTargetEncodings(AudioFormat)
     * see #getTargetFormats(AudioFormat.Encoding, AudioFormat)
     * see #isConversionSupported(AudioFormat, AudioFormat)
     * see #getAudioInputStream(AudioFormat.Encoding, AudioInputStream)
     */
    public static AudioInputStream getAudioInputStream(AudioFormat targetFormat,
                                                       AudioInputStream sourceStream) {

        if (sourceStream.getFormat().matches(targetFormat)) {
            return sourceStream;
        }

        List codecs = getFormatConversionProviders();

        for(int i = 0; i < codecs.size(); i++) {
            FormatConversionProvider codec = (FormatConversionProvider) codecs.get(i);
            if(codec.isConversionSupported(targetFormat,sourceStream.getFormat()) ) {
                return codec.getAudioInputStream(targetFormat,sourceStream);
            }
        }

        // we ran out of options...
        throw new IllegalArgumentException("Unsupported conversion: " + targetFormat + " from " + sourceStream.getFormat());
    }


    /**
     * Obtains the audio file format of the provided input stream.  The stream must
     * point to valid audio file data.  The implementation of this method may require
     * multiple parsers to examine the stream to determine whether they support it.
     * These parsers must be able to mark the stream, read enough data to determine whether they
     * support the stream, and, if not, reset the stream's read pointer to its original
     * position.  If the input stream does not support these operations, this method may fail
     * with an <code>IOException</code>.
     * @param stream the input stream from which file format information should be
     * extracted
     * @return an <code>AudioFileFormat</code> object describing the stream's audio file format
     * throws UnsupportedAudioFileException if the stream does not point to valid audio
     * file data recognized by the system
     * throws IOException if an input/output exception occurs
     * see InputStream#markSupported
     * see InputStream#mark
     */
    public static AudioFileFormat getAudioFileFormat(InputStream stream)
        throws UnsupportedAudioFileException, IOException {

        List providers = getAudioFileReaders();
        AudioFileFormat format = null;

        for(int i = 0; i < providers.size(); i++ ) {
            AudioFileReader reader = (AudioFileReader) providers.get(i);
            try {
                format = reader.getAudioFileFormat( stream ); // throws IOException
                break;
            } catch (UnsupportedAudioFileException e) {
                continue;
            }
        }

        if( format==null ) {
            throw new UnsupportedAudioFileException("file is not a supported file type");
        } else {
            return format;
        }
    }

    

    


    /**
     * Obtains an audio input stream from the provided input stream.  The stream must
     * point to valid audio file data.  The implementation of this method may
     * require multiple parsers to
     * examine the stream to determine whether they support it.  These parsers must
     * be able to mark the stream, read enough data to determine whether they
     * support the stream, and, if not, reset the stream's read pointer to its original
     * position.  If the input stream does not support these operation, this method may fail
     * with an <code>IOException</code>.
     * @param stream the input stream from which the <code>AudioInputStream</code> should be
     * constructed
     * @return an <code>AudioInputStream</code> object based on the audio file data contained
     * in the input stream.
     * throws UnsupportedAudioFileException if the stream does not point to valid audio
     * file data recognized by the system
     * throws IOException if an I/O exception occurs
     * see InputStream#markSupported
     * see InputStream#mark
     */
    public static AudioInputStream getAudioInputStream(InputStream stream)
        throws UnsupportedAudioFileException, IOException {

        List providers = getAudioFileReaders();
        AudioInputStream audioStream = null;

        for(int i = 0; i < providers.size(); i++ ) {
            AudioFileReader reader = (AudioFileReader) providers.get(i);
            try {
                audioStream = reader.getAudioInputStream( stream ); // throws IOException
                break;
            } catch (UnsupportedAudioFileException e) {
                continue;
            }
        }

        if( audioStream==null ) {
            throw new UnsupportedAudioFileException("could not get audio input stream from input stream");
        } else {
            return audioStream;
        }
    }

    

    


    


    


    


    


    


    


    // METHODS FOR INTERNAL IMPLEMENTATION USE

    /**
     * Obtains the set of MixerProviders on the system.
     */
    private static List getMixerProviders() {
        return getProviders(MixerProvider.class);
    }


    /**
     * Obtains the set of format converters (codecs, transcoders, etc.)
     * that are currently installed on the system.
     * @return an array of
     *  gervill.javax.sound.sampled.spi.FormatConversionProvider
     * FormatConversionProvider
     * objects representing the available format converters.  If no format
     * converters readers are available on the system, an array of length 0 is
     * returned.
     */
    private static List getFormatConversionProviders() {
        return getProviders(FormatConversionProvider.class);
    }


    /**
     * Obtains the set of audio file readers that are currently installed on the system.
     * @return a List of
     *  gervill.javax.sound.sampled.spi.AudioFileReader
     * AudioFileReader
     * objects representing the installed audio file readers.  If no audio file
     * readers are available on the system, an empty List is returned.
     */
    private static List getAudioFileReaders() {
        return getProviders(AudioFileReader.class);
    }


    /**
     * Obtains the set of services currently installed on the system
     * using sun.misc.Service, the SPI mechanism in 1.3.
     * @return a List of instances of providers for the requested service.
     * If no providers are available, a vector of length 0 will be returned.
     */
    private static List getProviders(Class providerClass) {
        return JDK13Services.getProviders(providerClass);
    }
}
