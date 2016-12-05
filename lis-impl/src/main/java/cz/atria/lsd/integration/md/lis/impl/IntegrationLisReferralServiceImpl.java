package cz.atria.lsd.integration.md.lis.impl;

import cz.atria.common.base.dao.query.QueryUtils;
import cz.atria.lsd.integration.md.lis.api.IntegrationLisReferral;
import cz.atria.lsd.integration.md.lis.api.IntegrationLisReferralService;
import cz.atria.md.referral.Referral;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * User: tnurtdinov
 * Date: 12.12.12
 * Time: 16:02
 */
@Service
@Transactional
public class IntegrationLisReferralServiceImpl implements IntegrationLisReferralService
{
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public IntegrationLisReferral saveIntegrationLisReferral(IntegrationLisReferral integrationLisReferral)
	{
		if (integrationLisReferral.getId() == null)
		{
			entityManager.persist(integrationLisReferral);
			return integrationLisReferral;
		}
		else
		{
			return entityManager.merge(integrationLisReferral);
		}
	}

	@Override
	public List<Referral> getReferralsOnSend()
	{

		return entityManager.createQuery(
				"select ref from Referral ref where ref.type.id = 7 " +
						"and not exists(select iref from IntegrationLisReferral iref where iref.referralId = ref.id)")
				.setMaxResults(1000).getResultList();
	}

	@Override
	public List<Referral> getNotSentReferrals()
	{
		return entityManager.createQuery(
						"select ref from Referral ref where ref.type.id = 7 " +
								"and exists(select iref from IntegrationLisReferral iref where iref.referralId = ref.id and iref.sampleId is null)")
						.setMaxResults(1000).getResultList();
	}

	@Override
	public List<IntegrationLisReferral> getNotAttachedIntegrationLisReferralList()
	{
		return entityManager.createQuery("select iref from IntegrationLisReferral iref  " +
				"where (iref.isAttached is  null or iref.isAttached = false) and iref.sampleId is not null")
				.setMaxResults(1000).getResultList();
	}

	@Override
	public IntegrationLisReferral getIntegrationLisReferral(Integer referralId)
	{
		TypedQuery<IntegrationLisReferral> query = entityManager.createQuery("select iref from IntegrationLisReferral iref" +
				" where iref.referralId = :referralId", IntegrationLisReferral.class).setParameter("referralId", referralId);
		return QueryUtils.getSingleResult(query);
	}
}
