package cz.atria.lsd.integration.md.lis.api;

import cz.atria.md.referral.Referral;

import java.util.List;

/**
 * User: tnurtdinov
 * Date: 12.12.12
 * Time: 16:00
 */
public interface IntegrationLisReferralService
{
	IntegrationLisReferral saveIntegrationLisReferral(IntegrationLisReferral integrationLisReferral);

	List<Referral> getReferralsOnSend();

	List<Referral> getNotSentReferrals();

	List<IntegrationLisReferral> getNotAttachedIntegrationLisReferralList();

	IntegrationLisReferral getIntegrationLisReferral(Integer referralId);
}
