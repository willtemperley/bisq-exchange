/*
 * This file is part of bisq.
 *
 * bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package io.bisq.core.dao.blockchain.parse;

import io.bisq.core.dao.blockchain.vo.TxOutput;
import io.bisq.core.dao.compensation.CompensationRequest;
import io.bisq.core.dao.compensation.CompensationRequestModel;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class IssuanceVerification {
    public static final long MIN_BSQ_ISSUANCE_AMOUNT = 1000;
    public static final long MAX_BSQ_ISSUANCE_AMOUNT = 10_000_000;

    private BsqChainState bsqChainState;
    private PeriodVerification periodVerification;
    private VotingVerification votingVerification;
    private CompensationRequestModel compensationRequestModel;

    @Inject
    public IssuanceVerification(BsqChainState bsqChainState,
                                PeriodVerification periodVerification,
                                VotingVerification votingVerification,
                                CompensationRequestModel compensationRequestModel) {
        this.bsqChainState = bsqChainState;
        this.periodVerification = periodVerification;
        this.votingVerification = votingVerification;
        this.compensationRequestModel = compensationRequestModel;
    }

    boolean maybeProcessData(List<TxOutput> outputs, int outputIndex) {
        if (outputIndex == 0 && outputs.size() >= 2) {
            TxOutput btcTxOutput = outputs.get(0);
            TxOutput bsqTxOutput = outputs.get(1);
            final String btcAddress = btcTxOutput.getAddress();
            final Optional<CompensationRequest> compensationRequest = compensationRequestModel.findByAddress(btcAddress);
            if (compensationRequest.isPresent()) {
                final CompensationRequest compensationRequest1 = compensationRequest.get();
                final long bsqAmount = bsqTxOutput.getValue();
                final long requestedBtc = compensationRequest1.getCompensationRequestPayload().getRequestedBtc().value;
                long alreadyFundedBtc = 0;
                final int height = btcTxOutput.getBlockHeight();
                Set<TxOutput> issuanceTxs = bsqChainState.containsIssuanceTxOutputsByBtcAddress(btcAddress);
                for (TxOutput txOutput : issuanceTxs) {
                    if (txOutput.getBlockHeight() < height ||
                            (txOutput.getBlockHeight() == height &&
                                    txOutput.getId().compareTo(btcTxOutput.getId()) == 1)) {
                        alreadyFundedBtc += txOutput.getValue();
                    }
                }
                final long btcAmount = btcTxOutput.getValue();
                if (periodVerification.isInSponsorPeriod(height) &&
                        bsqChainState.containsCompensationRequestBtcAddress(btcAddress) &&
                        votingVerification.isCompensationRequestAccepted(compensationRequest1) &&
                        alreadyFundedBtc + btcAmount <= requestedBtc &&
                        bsqAmount >= MIN_BSQ_ISSUANCE_AMOUNT && bsqAmount <= MAX_BSQ_ISSUANCE_AMOUNT &&
                        votingVerification.isConversionRateValid(height, btcAmount, bsqAmount)) {
                    bsqChainState.addIssuanceBtcTxOutput(btcTxOutput);
                    return true;
                }
            }
        }
        return false;
    }
}