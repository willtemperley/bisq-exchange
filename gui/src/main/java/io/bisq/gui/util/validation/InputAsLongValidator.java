/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package io.bisq.gui.util.validation;

import io.bisq.common.locale.Res;

public class InputAsLongValidator extends NumberValidator {

    protected long minValue = Long.MIN_VALUE;

    protected long maxValue = Long.MAX_VALUE;

    public void setMinValue(long minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(long maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public ValidationResult validate(String input) {
        ValidationResult result = validateIfNotEmpty(input);
        if (result.isValid) {
            input = cleanInput(input);
            result = validateIfNumber(input);
        }

        if (result.isValid) {
            result = validateIfNotZero(input)
                    .and(validateIfNotNegative(input))
                    .and(validateIfNotExceedsMaxValue(input))
                    .and(validateIfNotUnderMinValue(input));
        }

        return result;
    }

    protected ValidationResult validateIfNotExceedsMaxValue(String input) {
        try {
            final long val = Long.parseLong(input);
            if (val > maxValue)
                return new ValidationResult(false, Res.get("validation.tooLarge", maxValue));
            else
                return new ValidationResult(true);
        } catch (Throwable t) {
            return new ValidationResult(false, Res.get("validation.invalidInput", t.getMessage()));
        }
    }

    protected ValidationResult validateIfNotUnderMinValue(String input) {
        try {
            final long val = Long.parseLong(input);
            if (val < minValue)
                return new ValidationResult(false, Res.get("validation.tooSmall", minValue));
            else
                return new ValidationResult(true);
        } catch (Throwable t) {
            return new ValidationResult(false, Res.get("validation.invalidInput", t.getMessage()));
        }
    }
}
