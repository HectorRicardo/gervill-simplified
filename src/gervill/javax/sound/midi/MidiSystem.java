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

package gervill.javax.sound.midi;

import java.io.InputStream;
import java.io.IOException;

import java.util.List;
import gervill.javax.sound.midi.spi.MidiFileReader;
import gervill.javax.sound.midi.spi.SoundbankReader;
import gervill.javax.sound.midi.spi.MidiDeviceProvider;

import gervill.com.sun.media.sound.JDK13Services;
import gervill.com.sun.media.sound.ReferenceCountingDevice;
import gervill.com.sun.media.sound.MidiDeviceReceiverEnvelope;


/**
 * The <code>MidiSystem</code> class provides access to the installed MIDI
 * system resources, including devices such as synthesizers, sequencers, and
 * MIDI input and output ports.  A typical simple MIDI application might
 * begin by invoking one or more <code>MidiSystem</code> methods to learn
 * what devices are installed and to obtain the ones needed in that
 * application.
 * <p>
 * The class also has methods for reading files, streams, and  URLs that
 * contain standard MIDI file data or soundbanks.  You can query the
 * <code>MidiSystem</code> for the format of a specified MIDI file.
 * <p>
 * You cannot instantiate a <code>MidiSystem</code>; all the methods are
 * static.
 *
 * <p>Properties can be used to specify default MIDI devices.
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
 *  <caption>MIDI System Property Keys</caption>
 *  <tr>
 *   <th>Property Key</th>
 *   <th>Interface</th>
 *   <th>Affected Method</th>
 *  </tr>
 *  <tr>
 *   <td><code>gervill.javax.sound.midi.Receiver</code></td>
 *   <td> Receiver</td>
 *   <td> #getReceiver</td>
 *  </tr>
 *  <tr>
 *   <td><code>gervill.javax.sound.midi.Sequencer</code></td>
 *   <td> Sequencer</td>
 *   <td> #getSequencer</td>
 *  </tr>
 *  <tr>
 *   <td><code>gervill.javax.sound.midi.Synthesizer</code></td>
 *   <td> Synthesizer</td>
 *   <td> #getSynthesizer</td>
 *  </tr>
 *  <tr>
 *   <td><code>gervill.javax.sound.midi.Transmitter</code></td>
 *   <td> Transmitter</td>
 *   <td> #getTransmitter</td>
 *  </tr>
 * </table>
 *
 * The property value consists of the provider class name
 * and the device name, separated by the hash mark (&quot;#&quot;).
 * The provider class name is the fully-qualified
 * name of a concrete  gervill.javax.sound.midi.spi.MidiDeviceProvider
 * MIDI device provider class. The device name is matched against
 * the <code>String</code> returned by the <code>getName</code>
 * method of <code>MidiDevice.Info</code>.
 * Either the class name, or the device name may be omitted.
 * If only the class name is specified, the trailing hash mark
 * is optional.
 *
 * <p>If the provider class is specified, and it can be
 * successfully retrieved from the installed providers,
 * the list of
 * <code>MidiDevice.Info</code> objects is retrieved
 * from the provider. Otherwise, or when these devices
 * do not provide a subsequent match, the list is retrieved
 * from  #getMidiDeviceInfo to contain
 * all available <code>MidiDevice.Info</code> objects.
 *
 * <p>If a device name is specified, the resulting list of
 * <code>MidiDevice.Info</code> objects is searched:
 * the first one with a matching name, and whose
 * <code>MidiDevice</code> implements the
 * respective interface, will be returned.
 * If no matching <code>MidiDevice.Info</code> object
 * is found, or the device name is not specified,
 * the first suitable device from the resulting
 * list will be returned. For Sequencer and Synthesizer,
 * a device is suitable if it implements the respective
 * interface; whereas for Receiver and Transmitter, a device is
 * suitable if it
 * implements neither Sequencer nor Synthesizer and provides
 * at least one Receiver or Transmitter, respectively.
 *
 * For example, the property <code>gervill.javax.sound.midi.Receiver</code>
 * with a value
 * <code>&quot;gervill.com.sun.media.sound.MidiProvider#SunMIDI1&quot;</code>
 * will have the following consequences when
 * <code>getReceiver</code> is called:
 * if the class <code>gervill.com.sun.media.sound.MidiProvider</code> exists
 * in the list of installed MIDI device providers,
 * the first <code>Receiver</code> device with name
 * <code>&quot;SunMIDI1&quot;</code> will be returned. If it cannot
 * be found, the first <code>Receiver</code> from that provider
 * will be returned, regardless of name.
 * If there is none, the first <code>Receiver</code> with name
 * <code>&quot;SunMIDI1&quot;</code> in the list of all devices
 * (as returned by <code>getMidiDeviceInfo</code>) will be returned,
 * or, if not found, the first <code>Receiver</code> that can
 * be found in the list of all devices is returned.
 * If that fails, too, a <code>MidiUnavailableException</code>
 * is thrown.
 *
 * @author Kara Kytle
 * @author Florian Bomers
 * @author Matthias Pfisterer
 */
public class MidiSystem {

    /**
     * Private no-args constructor for ensuring against instantiation.
     */
    private MidiSystem() {
    }


    


    


    /**
     * Obtains a MIDI receiver from an external MIDI port
     * or other default device.
     * The returned receiver always implements
     * the {@code MidiDeviceReceiver} interface.
     *
     * <p>If the system property
     * <code>gervill.javax.sound.midi.Receiver</code>
     * is defined or it is defined in the file &quot;sound.properties&quot;,
     * it is used to identify the device that provides the default receiver.
     * For details, refer to the  MidiSystem class description.
     *
     * If a suitable MIDI port is not available, the Receiver is
     * retrieved from an installed synthesizer.
     *
     * <p>If a native receiver provided by the default device does not implement
     * the {@code MidiDeviceReceiver} interface, it will be wrapped in a
     * wrapper class that implements the {@code MidiDeviceReceiver} interface.
     * The corresponding {@code Receiver} method calls will be forwarded
     * to the native receiver.
     *
     * <p>If this method returns successfully, the 
     * gervill.javax.sound.midi.MidiDevice MidiDevice the
     * <code>Receiver</code> belongs to is opened implicitly, if it is
     * not already open. It is possible to close an implicitly opened
     * device by calling  gervill.javax.sound.midi.Receiver#close close
     * on the returned <code>Receiver</code>. All open <code>Receiver</code>
     * instances have to be closed in order to release system resources
     * hold by the <code>MidiDevice</code>. For a
     * detailed description of open/close behaviour see the class
     * description of  gervill.javax.sound.midi.MidiDevice MidiDevice.
     *
     *
     * @return the default MIDI receiver
     * throws MidiUnavailableException if the default receiver is not
     *         available due to resource restrictions,
     *         or no device providing receivers is installed in the system
     */
    public static Receiver getReceiver() throws MidiUnavailableException {
        // may throw MidiUnavailableException
        MidiDevice device = getDefaultDeviceWrapper(Receiver.class);
        Receiver receiver;
        if (device instanceof ReferenceCountingDevice) {
            receiver = ((ReferenceCountingDevice) device).getReceiverReferenceCounting();
        } else {
            receiver = device.getReceiver();
        }
        if (!(receiver instanceof MidiDeviceReceiver)) {
            receiver = new MidiDeviceReceiverEnvelope(device, receiver);
        }
        return receiver;
    }


    


    /**
     * Obtains the default synthesizer.
     *
     * <p>If the system property
     * <code>gervill.javax.sound.midi.Synthesizer</code>
     * is defined or it is defined in the file &quot;sound.properties&quot;,
     * it is used to identify the default synthesizer.
     * For details, refer to the  MidiSystem class description.
     *
     * @return the default synthesizer
     * throws MidiUnavailableException if the synthesizer is not
     *         available due to resource restrictions,
     *         or no synthesizer is installed in the system
     */
    public static Synthesizer getSynthesizer() throws MidiUnavailableException {
        // may throw MidiUnavailableException
        return (Synthesizer) getDefaultDeviceWrapper(Synthesizer.class);
    }


    



    /**
     * Constructs a MIDI sound bank by reading it from the specified stream.
     * The stream must point to
     * a valid MIDI soundbank file.  In general, MIDI soundbank providers may
     * need to read some data from the stream before determining whether they
     * support it.  These parsers must
     * be able to mark the stream, read enough data to determine whether they
     * support the stream, and, if not, reset the stream's read pointer to
     * its original position.  If the input stream does not support this,
     * this method may fail with an IOException.
     * @param stream the source of the sound bank data.
     * @return the sound bank
     * throws InvalidMidiDataException if the stream does not point to
     * valid MIDI soundbank data recognized by the system
     * throws IOException if an I/O error occurred when loading the soundbank
     * see InputStream#markSupported
     * see InputStream#mark
     */
    public static Soundbank getSoundbank(InputStream stream)
        throws InvalidMidiDataException, IOException {

        SoundbankReader sp = null;
        Soundbank s = null;

        List providers = getSoundbankReaders();

        for(int i = 0; i < providers.size(); i++) {
            sp = (SoundbankReader)providers.get(i);
            s = sp.getSoundbank(stream);

            if( s!= null) {
                return s;
            }
        }
        throw new InvalidMidiDataException("cannot get soundbank from stream");

    }


    


    



    


    


    


    /**
     * Obtains a MIDI sequence from the specified input stream.  The stream must
     * point to valid MIDI file data for a file type recognized
     * by the system.
     * <p>
     * This method and/or the code it invokes may need to read some data
     * from the stream to determine whether
     * its data format is supported.  The implementation may therefore
     * need to mark the stream, read enough data to determine whether it is in
     * a supported format, and reset the stream's read pointer to its original
     * position.  If the input stream does not permit this set of operations,
     * this method may fail with an <code>IOException</code>.
     * <p>
     * This operation can only succeed for files of a type which can be parsed
     * by an installed file reader.  It may fail with an InvalidMidiDataException
     * even for valid files if no compatible file reader is installed.  It
     * will also fail with an InvalidMidiDataException if a compatible file reader
     * is installed, but encounters errors while constructing the <code>Sequence</code>
     * object from the file data.
     *
     * @param stream the input stream from which the <code>Sequence</code>
     * should be constructed
     * @return a <code>Sequence</code> object based on the MIDI file data
     * contained in the input stream
     * throws InvalidMidiDataException if the stream does not point to
     * valid MIDI file data recognized by the system
     * throws IOException if an I/O exception occurs while accessing the
     * stream
     * see InputStream#markSupported
     * see InputStream#mark
     */
    public static Sequence getSequence(InputStream stream)
        throws InvalidMidiDataException, IOException {

        List providers = getMidiFileReaders();
        Sequence sequence = null;

        for(int i = 0; i < providers.size(); i++) {
            MidiFileReader reader = (MidiFileReader) providers.get(i);
            try {
                sequence = reader.getSequence( stream ); // throws IOException
                break;
            } catch (InvalidMidiDataException e) {
                continue;
            }
        }

        if( sequence==null ) {
            throw new InvalidMidiDataException("could not get sequence from input stream");
        } else {
            return sequence;
        }
    }


    


    


    


    


    


    


    


    



    // HELPER METHODS

    private static List getMidiDeviceProviders() {
        return getProviders(MidiDeviceProvider.class);
    }


    private static List getSoundbankReaders() {
        return getProviders(SoundbankReader.class);
    }


    private static List getMidiFileReaders() {
        return getProviders(MidiFileReader.class);
    }


    /** Attempts to locate and return a default MidiDevice of the specified
     * type.
     *
     * This method wraps  #getDefaultDevice. It catches the
     * <code>IllegalArgumentException</code> thrown by
     * <code>getDefaultDevice</code> and instead throws a
     * <code>MidiUnavailableException</code>, with the catched
     * exception chained.
     *
     * @param deviceClass The requested device type, one of Synthesizer.class,
     * Sequencer.class, Receiver.class or Transmitter.class.
     * throws  MidiUnavalableException on failure.
     */
    private static MidiDevice getDefaultDeviceWrapper(Class deviceClass)
        throws MidiUnavailableException{
        try {
            return getDefaultDevice(deviceClass);
        } catch (IllegalArgumentException iae) {
            MidiUnavailableException mae = new MidiUnavailableException();
            mae.initCause(iae);
            throw mae;
        }
    }


    /** Attempts to locate and return a default MidiDevice of the specified
     * type.
     *
     * @param deviceClass The requested device type, one of Synthesizer.class,
     * Sequencer.class, Receiver.class or Transmitter.class.
     * throws  IllegalArgumentException on failure.
     */
    private static MidiDevice getDefaultDevice(Class deviceClass) {
        List providers = getMidiDeviceProviders();
        String providerClassName = JDK13Services.getDefaultProviderClassName(deviceClass);
        String instanceName = JDK13Services.getDefaultInstanceName(deviceClass);
        MidiDevice device;

        if (providerClassName != null) {
            MidiDeviceProvider defaultProvider = getNamedProvider(providerClassName, providers);
            if (defaultProvider != null) {
                if (instanceName != null) {
                    device = getNamedDevice(instanceName, defaultProvider, deviceClass);
                    if (device != null) {
                        return device;
                    }
                }
                device = getFirstDevice(defaultProvider, deviceClass);
                if (device != null) {
                    return device;
                }
            }
        }

        /* Provider class not specified or cannot be found, or
           provider class specified, and no appropriate device available or
           provider class and instance specified and instance cannot be found or is not appropriate */
        if (instanceName != null) {
            device = getNamedDevice(instanceName, providers, deviceClass);
            if (device != null) {
                return device;
            }
        }

        /* No default are specified, or if something is specified, everything
           failed. */
        device = getFirstDevice(providers, deviceClass);
        if (device != null) {
            return device;
        }
        throw new IllegalArgumentException("Requested device not installed");
    }



    /** Return a MidiDeviceProcider of a given class from the list of
        MidiDeviceProviders.

        @param providerClassName The class name of the provider to be returned.
        @param provider The list of MidiDeviceProviders that is searched.
        @return A MidiDeviceProvider of the requested class, or null if none
        is found.
    */
    private static MidiDeviceProvider getNamedProvider(String providerClassName, List providers) {
        for(int i = 0; i < providers.size(); i++) {
            MidiDeviceProvider provider = (MidiDeviceProvider) providers.get(i);
            if (provider.getClass().getName().equals(providerClassName)) {
                return provider;
            }
        }
        return null;
    }


    /** Return a MidiDevice with a given name from a given MidiDeviceProvider.
        @param deviceName The name of the MidiDevice to be returned.
        @param provider The MidiDeviceProvider to check for MidiDevices.
        @param deviceClass The requested device type, one of Synthesizer.class,
        Sequencer.class, Receiver.class or Transmitter.class.

        @return A MidiDevice matching the requirements, or null if none is found.
    */
    private static MidiDevice getNamedDevice(String deviceName,
                                             MidiDeviceProvider provider,
                                             Class deviceClass) {
        MidiDevice device;
        // try to get MIDI port
        device = getNamedDevice(deviceName, provider, deviceClass,
                                 false, false);
        if (device != null) {
            return device;
        }

        if (deviceClass == Receiver.class) {
            // try to get Synthesizer
            device = getNamedDevice(deviceName, provider, deviceClass,
                                     true, false);
            if (device != null) {
                return device;
            }
        }

        return null;
    }


    /** Return a MidiDevice with a given name from a given MidiDeviceProvider.
      @param deviceName The name of the MidiDevice to be returned.
      @param provider The MidiDeviceProvider to check for MidiDevices.
      @param deviceClass The requested device type, one of Synthesizer.class,
      Sequencer.class, Receiver.class or Transmitter.class.

      @return A MidiDevice matching the requirements, or null if none is found.
     */
    private static MidiDevice getNamedDevice(String deviceName,
                                             MidiDeviceProvider provider,
                                             Class deviceClass,
                                             boolean allowSynthesizer,
                                             boolean allowSequencer) {
        MidiDevice.Info[] infos = provider.getDeviceInfo();
        for (int i = 0; i < infos.length; i++) {
            if (infos[i].getName().equals(deviceName)) {
                MidiDevice device = provider.getDevice(infos[i]);
                if (isAppropriateDevice(device, deviceClass,
                                        allowSynthesizer, allowSequencer)) {
                    return device;
                }
            }
        }
        return null;
    }


    /** Return a MidiDevice with a given name from a list of
        MidiDeviceProviders.
        @param deviceName The name of the MidiDevice to be returned.
        @param providers The List of MidiDeviceProviders to check for
        MidiDevices.
        @param deviceClass The requested device type, one of Synthesizer.class,
        Sequencer.class, Receiver.class or Transmitter.class.
        @return A Mixer matching the requirements, or null if none is found.
    */
    private static MidiDevice getNamedDevice(String deviceName,
                                             List providers,
                                             Class deviceClass) {
        MidiDevice device;
        // try to get MIDI port
        device = getNamedDevice(deviceName, providers, deviceClass,
                                 false, false);
        if (device != null) {
            return device;
        }

        if (deviceClass == Receiver.class) {
            // try to get Synthesizer
            device = getNamedDevice(deviceName, providers, deviceClass,
                                     true, false);
            if (device != null) {
                return device;
            }
        }

        return null;
    }


    /** Return a MidiDevice with a given name from a list of
        MidiDeviceProviders.
        @param deviceName The name of the MidiDevice to be returned.
        @param providers The List of MidiDeviceProviders to check for
        MidiDevices.
        @param deviceClass The requested device type, one of Synthesizer.class,
        Sequencer.class, Receiver.class or Transmitter.class.
        @return A Mixer matching the requirements, or null if none is found.
     */
    private static MidiDevice getNamedDevice(String deviceName,
                                             List providers,
                                             Class deviceClass,
                                             boolean allowSynthesizer,
                                             boolean allowSequencer) {
        for(int i = 0; i < providers.size(); i++) {
            MidiDeviceProvider provider = (MidiDeviceProvider) providers.get(i);
            MidiDevice device = getNamedDevice(deviceName, provider,
                                               deviceClass,
                                               allowSynthesizer,
                                               allowSequencer);
            if (device != null) {
                return device;
            }
        }
        return null;
    }


    /** From a given MidiDeviceProvider, return the first appropriate device.
        @param provider The MidiDeviceProvider to check for MidiDevices.
        @param deviceClass The requested device type, one of Synthesizer.class,
        Sequencer.class, Receiver.class or Transmitter.class.
        @return A MidiDevice is considered appropriate, or null if no
        appropriate device is found.
    */
    private static MidiDevice getFirstDevice(MidiDeviceProvider provider,
                                             Class deviceClass) {
        MidiDevice device;
        // try to get MIDI port
        device = getFirstDevice(provider, deviceClass,
                                false, false);
        if (device != null) {
            return device;
        }

        if (deviceClass == Receiver.class) {
            // try to get Synthesizer
            device = getFirstDevice(provider, deviceClass,
                                    true, false);
            if (device != null) {
                return device;
            }
        }

        return null;
    }


    /** From a given MidiDeviceProvider, return the first appropriate device.
        @param provider The MidiDeviceProvider to check for MidiDevices.
        @param deviceClass The requested device type, one of Synthesizer.class,
        Sequencer.class, Receiver.class or Transmitter.class.
        @return A MidiDevice is considered appropriate, or null if no
        appropriate device is found.
     */
    private static MidiDevice getFirstDevice(MidiDeviceProvider provider,
                                             Class deviceClass,
                                             boolean allowSynthesizer,
                                             boolean allowSequencer) {
        MidiDevice.Info[] infos = provider.getDeviceInfo();
        for (int j = 0; j < infos.length; j++) {
            MidiDevice device = provider.getDevice(infos[j]);
            if (isAppropriateDevice(device, deviceClass,
                                    allowSynthesizer, allowSequencer)) {
                return device;
            }
        }
        return null;
    }


    /** From a List of MidiDeviceProviders, return the first appropriate
        MidiDevice.
        @param providers The List of MidiDeviceProviders to search.
        @param deviceClass The requested device type, one of Synthesizer.class,
        Sequencer.class, Receiver.class or Transmitter.class.
        @return A MidiDevice that is considered appropriate, or null
        if none is found.
    */
    private static MidiDevice getFirstDevice(List providers,
                                             Class deviceClass) {
        MidiDevice device;
        // try to get MIDI port
        device = getFirstDevice(providers, deviceClass,
                                false, false);
        if (device != null) {
            return device;
        }

        if (deviceClass == Receiver.class) {
            // try to get Synthesizer
            device = getFirstDevice(providers, deviceClass,
                                    true, false);
            if (device != null) {
                return device;
            }
        }

        return null;
    }


    /** From a List of MidiDeviceProviders, return the first appropriate
        MidiDevice.
        @param providers The List of MidiDeviceProviders to search.
        @param deviceClass The requested device type, one of Synthesizer.class,
        Sequencer.class, Receiver.class or Transmitter.class.
        @return A MidiDevice that is considered appropriate, or null
        if none is found.
     */
    private static MidiDevice getFirstDevice(List providers,
                                             Class deviceClass,
                                             boolean allowSynthesizer,
                                             boolean allowSequencer) {
        for(int i = 0; i < providers.size(); i++) {
            MidiDeviceProvider provider = (MidiDeviceProvider) providers.get(i);
            MidiDevice device = getFirstDevice(provider, deviceClass,
                                               allowSynthesizer,
                                               allowSequencer);
            if (device != null) {
                return device;
            }
        }
        return null;
    }


    /** Checks if a MidiDevice is appropriate.
        If deviceClass is Synthesizer or Sequencer, a device implementing
        the respective interface is considered appropriate. If deviceClass
        is Receiver or Transmitter, a device is considered appropriate if
        it implements neither Synthesizer nor Transmitter, and if it can
        provide at least one Receiver or Transmitter, respectively.

        @param device the MidiDevice to test
        @param allowSynthesizer if true, Synthesizers are considered
        appropriate. Otherwise only pure MidiDevices are considered
        appropriate (unless allowSequencer is true). This flag only has an
        effect for deviceClass Receiver and Transmitter. For other device
        classes (Sequencer and Synthesizer), this flag has no effect.
        @param allowSequencer if true, Sequencers are considered
        appropriate. Otherwise only pure MidiDevices are considered
        appropriate (unless allowSynthesizer is true). This flag only has an
        effect for deviceClass Receiver and Transmitter. For other device
        classes (Sequencer and Synthesizer), this flag has no effect.
        @return true if the device is considered appropriate according to the
        rules given above, false otherwise.
    */
    private static boolean isAppropriateDevice(MidiDevice device,
                                               Class deviceClass,
                                               boolean allowSynthesizer,
                                               boolean allowSequencer) {
        if (deviceClass.isInstance(device)) {
            // This clause is for deviceClass being either Synthesizer
            // or Sequencer.
            return true;
        } else {
            // Now the case that deviceClass is Transmitter or
            // Receiver. If neither allowSynthesizer nor allowSequencer is
            // true, we require device instances to be
            // neither Synthesizer nor Sequencer, since we only want
            // devices representing MIDI ports.
            // Otherwise, the respective type is accepted, too
            if ( (! (device instanceof Sequencer) &&
                  ! (device instanceof Synthesizer) ) ||
                 ((device instanceof Sequencer) && allowSequencer) ||
                 ((device instanceof Synthesizer) && allowSynthesizer)) {
                // And of cource, the device has to be able to provide
                // Receivers or Transmitters.
                if ((deviceClass == Receiver.class &&
                     device.getMaxReceivers() != 0) ||
                    (deviceClass == Transmitter.class &&
                     device.getMaxTransmitters() != 0)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Obtains the set of services currently installed on the system
     * using sun.misc.Service, the SPI mechanism in 1.3.
     * @return a List of instances of providers for the requested service.
     * If no providers are available, a List of length 0 will be returned.
     */
    private static List getProviders(Class providerClass) {
        return JDK13Services.getProviders(providerClass);
    }
}
