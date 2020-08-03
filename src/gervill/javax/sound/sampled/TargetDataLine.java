/*
 * Copyright (c) 1999, 2003, Oracle and/or its affiliates. All rights reserved.
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

/**
 * A target data line is a type of <code> DataLine</code> from which
 * audio data can be read.  The most common example is a data line that gets
 * its data from an audio capture device.  (The device is implemented as a
 * mixer that writes to the target data line.)
 * <p>
 * Note that the naming convention for this interface reflects the relationship
 * between the line and its mixer.  From the perspective of an application,
 * a target data line may act as a source for audio data.
 * <p>
 * The target data line can be obtained from a mixer by invoking the
 * <code> Mixer#getLine getLine</code>
 * method of <code>Mixer</code> with an appropriate
 * <code> DataLine.Info</code> object.
 * <p>
 * The <code>TargetDataLine</code> interface provides a method for reading the
 * captured data from the target data line's buffer.Applications
 * that record audio should read data from the target data line quickly enough
 * to keep the buffer from overflowing, which could cause discontinuities in
 * the captured data that are perceived as clicks.  Applications can use the
 * <code> DataLine#available available</code> method defined in the
 * <code>DataLine</code> interface to determine the amount of data currently
 * queued in the data line's buffer.  If the buffer does overflow,
 * the oldest queued data is discarded and replaced by new data.
 *
 * @author Kara Kytle
 * see Mixer
 * see DataLine
 * see SourceDataLine
 * @since 1.3
 */
public interface TargetDataLine extends DataLine {


    


    


    /**
     * Reads audio data from the data line's input buffer.   The requested
     * number of bytes is read into the specified array, starting at
     * the specified offset into the array in bytes.  This method blocks until
     * the requested amount of data has been read.  However, if the data line
     * is closed, stopped, drained, or flushed before the requested amount has
     * been read, the method no longer blocks, but returns the number of bytes
     * read thus far.
     * <p>
     * The number of bytes that can be read without blocking can be ascertained
     * using the <code> DataLine#available available</code> method of the
     * <code>DataLine</code> interface.  (While it is guaranteed that
     * this number of bytes can be read without blocking, there is no guarantee
     * that attempts to read additional data will block.)
     * <p>
     * The number of bytes to be read must represent an integral number of
     * sample frames, such that:
     * <br>
     * <center><code>[ bytes read ] % [frame size in bytes ] == 0</code></center>
     * <br>
     * The return value will always meet this requirement.  A request to read a
     * number of bytes representing a non-integral number of sample frames cannot
     * be fulfilled and may result in an IllegalArgumentException.
     *
     * @param b a byte array that will contain the requested input data when
     * this method returns
     * @param off the offset from the beginning of the array, in bytes
     * @param len the requested number of bytes to read
     * @return the number of bytes actually read
     * throws IllegalArgumentException if the requested number of bytes does
     * not represent an integral number of sample frames.
     * or if <code>len</code> is negative.
     * throws ArrayIndexOutOfBoundsException if <code>off</code> is negative,
     * or <code>off+len</code> is greater than the length of the array
     * <code>b</code>.
     *
     * see SourceDataLine#write
     * see DataLine#available
     */
    public int read(byte[] b, int off, int len);

    /**
     * Obtains the number of sample frames of audio data that can be read from
     * the target data line without blocking.  Note that the return value
     * measures sample frames, not bytes.
     * @return the number of sample frames currently available for reading
     * see SourceDataLine#availableWrite
     */
    //public int availableRead();
}
