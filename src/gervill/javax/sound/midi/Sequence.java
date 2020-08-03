/*
 * Copyright (c) 1999, 2004, Oracle and/or its affiliates. All rights reserved.
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

import java.util.Vector;


/**
 * A <code>Sequence</code> is a data structure containing musical
 * information (often an entire song or composition) that can be played
 * back by a <code> Sequencer</code> object. Specifically, the
 * <code>Sequence</code> contains timing
 * information and one or more tracks.  Each <code> Track track</code> consists of a
 * series of MIDI events (such as note-ons, note-offs, program changes, and meta-events).
 * The sequence's timing information specifies the type of unit that is used
 * to time-stamp the events in the sequence.
 * <p>
 * A <code>Sequence</code> can be created from a MIDI file by reading the file
 * into an input stream and invoking one of the <code>getSequence</code> methods of
 *  MidiSystem.  A sequence can also be built from scratch by adding new
 * <code>Tracks</code> to an empty <code>Sequence</code>, and adding
 * <code> MidiEvent</code> objects to these <code>Tracks</code>.
 *
 * see Sequencer#setSequence(java.io.InputStream stream)
 * see Sequencer#setSequence(Sequence sequence)
 * see Track#add(MidiEvent)
 * see MidiFileFormat
 *
 * @author Kara Kytle
 */
public class Sequence {


    // Timing types

    /**
     * The tempo-based timing type, for which the resolution is expressed in pulses (ticks) per quarter note.
     * see #Sequence(float, int)
     */
    public static final float PPQ                                                       = 0.0f;


    // Variables

    /**
     * The timing division type of the sequence.
     * see #PPQ
     * see #SMPTE_24
     * see #SMPTE_25
     * see #SMPTE_30DROP
     * see #SMPTE_30
     * see #getDivisionType
     */
    private float divisionType;

    /**
     * The timing resolution of the sequence.
     * see #getResolution
     */
    private int resolution;

    /**
     * The MIDI tracks in this sequence.
     * see #getTracks
     */
    private Vector<Track> tracks = new Vector<Track>();


    


    


    /**
     * Obtains the timing division type for this sequence.
     * @return the division type (PPQ or one of the SMPTE types)
     *
     * see #PPQ
     * see #SMPTE_24
     * see #SMPTE_25
     * see #SMPTE_30DROP
     * see #SMPTE_30
     * see #Sequence(float, int)
     * see MidiFileFormat#getDivisionType()
     */
    public float getDivisionType() {
        return divisionType;
    }


    /**
     * Obtains the timing resolution for this sequence.
     * If the sequence's division type is PPQ, the resolution is specified in ticks per beat.
     * For SMTPE timing, the resolution is specified in ticks per frame.
     *
     * @return the number of ticks per beat (PPQ) or per frame (SMPTE)
     * see #getDivisionType
     * see #Sequence(float, int)
     * see MidiFileFormat#getResolution()
     */
    public int getResolution() {
        return resolution;
    }


    


    


    /**
     * Obtains an array containing all the tracks in this sequence.
     * If the sequence contains no tracks, an array of length 0 is returned.
     * @return the array of tracks
     *
     * see #createTrack
     * see #deleteTrack
     */
    public Track[] getTracks() {

        return (Track[]) tracks.toArray(new Track[tracks.size()]);
    }


    /**
     * Obtains the duration of this sequence, expressed in microseconds.
     * @return this sequence's duration in microseconds.
     */
    public long getMicrosecondLength() {

        return gervill.com.sun.media.sound.MidiUtils.tick2microsecond(this, getTickLength(), null);
    }


    /**
     * Obtains the duration of this sequence, expressed in MIDI ticks.
     *
     * @return this sequence's length in ticks
     *
     * see #getMicrosecondLength
     */
    public long getTickLength() {

        long length = 0;

        synchronized(tracks) {

            for(int i=0; i<tracks.size(); i++ ) {
                long temp = ((Track)tracks.elementAt(i)).ticks();
                if( temp>length ) {
                    length = temp;
                }
            }
            return length;
        }
    }


    /**
     * Obtains a list of patches referenced in this sequence.
     * This patch list may be used to load the required
     * <code> Instrument</code> objects
     * into a <code> Synthesizer</code>.
     *
     * @return an array of <code> Patch</code> objects used in this sequence
     *
     * see Synthesizer#loadInstruments(Soundbank, Patch[])
     */
    public Patch[] getPatchList() {

        // $$kk: 04.09.99: need to implement!!
        return new Patch[0];
    }
}
