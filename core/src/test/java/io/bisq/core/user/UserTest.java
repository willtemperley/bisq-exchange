package io.bisq.core.user;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
@Slf4j
public class UserTest {
    @Test
    public void testGetStringFromBytes() {
        assertEquals("00", User.getStringFromBytes(new byte[]{0}));
        assertEquals("0e", User.getStringFromBytes(new byte[]{14}));
        assertEquals("1b", User.getStringFromBytes(new byte[]{27}));
        assertEquals("1E1621042C3742", User.getStringFromBytes(new byte[]{30, 22, 33, 4, 44, 55, 66}));
        assertEquals("84685803", User.getStringFromBytes(new byte[]{30, 22, 33, 4, 44, 55, 65}));
        assertEquals("1e1621042c3740", User.getStringFromBytes(new byte[]{30, 22, 33, 4, 44, 55, 64}));
    }
}
