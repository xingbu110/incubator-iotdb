/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.iotdb.tsfile.file.metadata.statistics;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.utils.BytesUtils;
import org.apache.iotdb.tsfile.utils.ReadWriteIOUtils;

public class DoubleStatistics extends Statistics<Double> {

  private double minValue;
  private double maxValue;
  private double firstValue;
  private double lastValue;
  private double sumValue;

  @Override
  public TSDataType getType() {
    return TSDataType.DOUBLE;
  }

  @Override
  public int getStatsSize() {
    return 40;
  }

  /**
   * initialize double statistics.
   *
   * @param min   min value
   * @param max   max value
   * @param first the first value
   * @param last  the last value
   * @param sum   sum value
   */
  private void initializeStats(double min, double max, double first, double last, double sum) {
    this.minValue = min;
    this.maxValue = max;
    this.firstValue = first;
    this.lastValue = last;
    this.sumValue = sum;
  }

  private void updateStats(double minValue, double maxValue, double lastValue, double sumValue) {
    if (minValue < this.minValue) {
      this.minValue = minValue;
    }
    if (maxValue > this.maxValue) {
      this.maxValue = maxValue;
    }
    this.sumValue += sumValue;
    this.lastValue = lastValue;
  }

  @Override
  public void setMinMaxFromBytes(byte[] minBytes, byte[] maxBytes) {
    minValue = BytesUtils.bytesToDouble(minBytes);
    maxValue = BytesUtils.bytesToDouble(maxBytes);
  }

  @Override
  void updateStats(double value) {
    if (this.isEmpty) {
      initializeStats(value, value, value, value, value);
      isEmpty = false;
    } else {
      updateStats(value, value, value, value);
    }
  }

  @Override
  void updateStats(double[] values, int batchSize) {
    for (int i = 0; i < batchSize; i++) {
      updateStats(values[i]);
    }
  }

  @Override
  public Double getMinValue() {
    return minValue;
  }

  @Override
  public Double getMaxValue() {
    return maxValue;
  }

  @Override
  public Double getFirstValue() {
    return firstValue;
  }

  @Override
  public Double getLastValue() {
    return lastValue;
  }

  @Override
  public double getSumValue() {
    return sumValue;
  }

  @Override
  protected void mergeStatisticsValue(Statistics stats) {
    DoubleStatistics doubleStats = (DoubleStatistics) stats;
    if (this.isEmpty) {
      initializeStats(doubleStats.getMinValue(), doubleStats.getMaxValue(), doubleStats.getFirstValue(),
          doubleStats.getLastValue(), doubleStats.getSumValue());
      isEmpty = false;
    } else {
      updateStats(doubleStats.getMinValue(), doubleStats.getMaxValue(),
          doubleStats.getLastValue(), doubleStats.getSumValue());
    }
  }

  @Override
  public byte[] getMinValueBytes() {
    return BytesUtils.doubleToBytes(minValue);
  }

  @Override
  public byte[] getMaxValueBytes() {
    return BytesUtils.doubleToBytes(maxValue);
  }

  @Override
  public byte[] getFirstValueBytes() {
    return BytesUtils.doubleToBytes(firstValue);
  }

  @Override
  public byte[] getLastValueBytes() {
    return BytesUtils.doubleToBytes(lastValue);
  }

  @Override
  public byte[] getSumValueBytes() {
    return BytesUtils.doubleToBytes(sumValue);
  }

  @Override
  public ByteBuffer getMinValueBuffer() {
    return ReadWriteIOUtils.getByteBuffer(minValue);
  }

  @Override
  public ByteBuffer getMaxValueBuffer() {
    return ReadWriteIOUtils.getByteBuffer(maxValue);
  }

  @Override
  public ByteBuffer getFirstValueBuffer() {
    return ReadWriteIOUtils.getByteBuffer(firstValue);
  }

  @Override
  public ByteBuffer getLastValueBuffer() {
    return ReadWriteIOUtils.getByteBuffer(lastValue);
  }

  @Override
  public ByteBuffer getSumValueBuffer() {
    return ReadWriteIOUtils.getByteBuffer(sumValue);
  }

  @Override
  public int serializeStats(OutputStream outputStream) throws IOException {
    int byteLen = 0;
    byteLen += ReadWriteIOUtils.write(minValue, outputStream);
    byteLen += ReadWriteIOUtils.write(maxValue, outputStream);
    byteLen += ReadWriteIOUtils.write(firstValue, outputStream);
    byteLen += ReadWriteIOUtils.write(lastValue, outputStream);
    byteLen += ReadWriteIOUtils.write(sumValue, outputStream);
    return byteLen;
  }

  @Override
  void deserialize(InputStream inputStream) throws IOException {
    this.minValue = ReadWriteIOUtils.readDouble(inputStream);
    this.maxValue = ReadWriteIOUtils.readDouble(inputStream);
    this.firstValue = ReadWriteIOUtils.readDouble(inputStream);
    this.lastValue = ReadWriteIOUtils.readDouble(inputStream);
    this.sumValue = ReadWriteIOUtils.readDouble(inputStream);
  }

  @Override
  void deserialize(ByteBuffer byteBuffer) {
    this.minValue = ReadWriteIOUtils.readDouble(byteBuffer);
    this.maxValue = ReadWriteIOUtils.readDouble(byteBuffer);
    this.firstValue = ReadWriteIOUtils.readDouble(byteBuffer);
    this.lastValue = ReadWriteIOUtils.readDouble(byteBuffer);
    this.sumValue = ReadWriteIOUtils.readDouble(byteBuffer);
  }

  @Override
  public String toString() {
    return super.toString() + " [minValue:" + minValue + ",maxValue:" + maxValue + ",firstValue:" + firstValue +
        ",lastValue:" + lastValue + ",sumValue:" + sumValue + "]";
  }
}
