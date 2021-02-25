/*
 * Copyright 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.intkilow.feat.qrscan.core.oned;

import java.util.Map;

import top.intkilow.feat.qrscan.core.BarcodeFormat;
import top.intkilow.feat.qrscan.core.BinaryBitmap;
import top.intkilow.feat.qrscan.core.ChecksumException;
import top.intkilow.feat.qrscan.core.DecodeHintType;
import top.intkilow.feat.qrscan.core.FormatException;
import top.intkilow.feat.qrscan.core.NotFoundException;
import top.intkilow.feat.qrscan.core.Result;
import top.intkilow.feat.qrscan.core.common.BitArray;

/**
 * <p>Implements decoding of the UPC-A format.</p>
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class UPCAReader extends UPCEANReader {

    private final UPCEANReader ean13Reader = new EAN13Reader();

    @Override
    public Result decodeRow(int rowNumber,
                            BitArray row,
                            int[] startGuardRange,
                            Map<DecodeHintType, ?> hints)
            throws NotFoundException, FormatException, ChecksumException {
        return maybeReturnResult(ean13Reader.decodeRow(rowNumber, row, startGuardRange, hints));
    }

    @Override
    public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints)
            throws NotFoundException, FormatException, ChecksumException {
        return maybeReturnResult(ean13Reader.decodeRow(rowNumber, row, hints));
    }

    @Override
    public Result decode(BinaryBitmap image) throws NotFoundException, FormatException {
        return maybeReturnResult(ean13Reader.decode(image));
    }

    @Override
    public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints)
            throws NotFoundException, FormatException {
        return maybeReturnResult(ean13Reader.decode(image, hints));
    }

    @Override
    BarcodeFormat getBarcodeFormat() {
        return BarcodeFormat.UPC_A;
    }

    @Override
    protected int decodeMiddle(BitArray row, int[] startRange, StringBuilder resultString)
            throws NotFoundException {
        return ean13Reader.decodeMiddle(row, startRange, resultString);
    }

    private static Result maybeReturnResult(Result result) throws FormatException {
        String text = result.getText();
        if (text.charAt(0) == '0') {
            Result upcaResult = new Result(text.substring(1), null, result.getResultPoints(), BarcodeFormat.UPC_A);
            if (result.getResultMetadata() != null) {
                upcaResult.putAllMetadata(result.getResultMetadata());
            }
            return upcaResult;
        } else {
            throw FormatException.getFormatInstance();
        }
    }

}
