package cz.atria.lsd.integration.md.lis.api;

import javax.persistence.*;

/**
 * User: tnurtdinov
 * Date: 12.12.12
 * Time: 15:43
 */
@Entity
@Table (name = "i_lis_referral")
public class IntegrationLisReferral
{
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "i_lis_referral_id_gen")
	@SequenceGenerator (name = "i_lis_referral_id_gen", sequenceName = "i_lis_referral_seq",
			allocationSize = 1)
	private Integer id;

	@Column(name = "referral_id")
	private Integer referralId;

	@Column(name = "sample_id")
	private Integer sampleId;

	@Column(name = "is_attached")
	private Boolean isAttached;

	public IntegrationLisReferral()
	{
	}

	public IntegrationLisReferral(Integer referralId)
	{
		this.referralId = referralId;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Integer getReferralId()
	{
		return referralId;
	}

	public void setReferralId(Integer referralId)
	{
		this.referralId = referralId;
	}

	public Integer getSampleId()
	{
		return sampleId;
	}

	public void setSampleId(Integer sampleId)
	{
		this.sampleId = sampleId;
	}

	public Boolean getAttached()
	{
		return isAttached;
	}

	public void setAttached(Boolean attached)
	{
		isAttached = attached;
	}
}
