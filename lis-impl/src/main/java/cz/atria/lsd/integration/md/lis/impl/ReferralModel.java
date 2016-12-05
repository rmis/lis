package cz.atria.lsd.integration.md.lis.impl;

import cz.atria.md.referral.Referral;

import java.util.Date;

/**
 * User: tnurtdinov
 * Date: 05.12.12
 * Time: 15:01
 */

public class ReferralModel
{
	private Integer refId;

	private Integer labId;

	private String pid;

	private String curdNum;

	private String fam;

	private String name;

	private String otch;

	private Date birthDay;

	private Integer sex;

	private Integer docId;

	private String docName;

	private String diagn;

	public ReferralModel(Referral referral)
	{
		//refid
		refId = referral.getId();
		// lab_id
		labId = (referral.getReceivingOrganization() != null) ? referral.getReceivingOrganization()
				.getId() : null;
		//pid
		pid = referral.getPatient().getIndividual().getUid();
		//card_num
		curdNum = referral.getPatient().getIndividual().getUid();
		//fam
		fam = referral.getPatient().getIndividual().getSurname();
		//name
		name = referral.getPatient().getIndividual().getName();
		//otch
		otch = referral.getPatient().getIndividual().getPatrName();
		//birthday
		birthDay = referral.getPatient().getIndividual().getBirthDate();
		//sex
		sex = (referral.getPatient().getIndividual().getGender() != null) ? referral.getPatient().getIndividual()
				.getGender().getId() : null;
		//doc_id
		docId = (referral.getDoctor() != null) ? referral.getDoctor().getEmployeePosition().getEmployee()
				.getId() : null;
		//doc_name
		docName = (referral.getDoctor() != null) ? referral.getDoctor().getEmployeePosition().getEmployee()
				.getIndividual().getFullName() : null;
		//diagn
		diagn = (referral.getDiagnosis() != null) ? referral.getDiagnosis().getCode() : null;
	}

	public Integer getRefId()
	{
		return refId;
	}

	public void setRefId(Integer refId)
	{
		this.refId = refId;
	}

	public Integer getLabId()
	{
		return labId;
	}

	public void setLabId(Integer labId)
	{
		this.labId = labId;
	}

	public String getPid()
	{
		return pid;
	}

	public void setPid(String pid)
	{
		this.pid = pid;
	}

	public String getCurdNum()
	{
		return curdNum;
	}

	public void setCurdNum(String curdNum)
	{
		this.curdNum = curdNum;
	}

	public String getFam()
	{
		return fam;
	}

	public void setFam(String fam)
	{
		this.fam = fam;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getOtch()
	{
		return otch;
	}

	public void setOtch(String otch)
	{
		this.otch = otch;
	}

	public Date getBirthDay()
	{
		return birthDay;
	}

	public void setBirthDay(Date birthDay)
	{
		this.birthDay = birthDay;
	}

	public Integer getSex()
	{
		return sex;
	}

	public void setSex(Integer sex)
	{
		this.sex = sex;
	}

	public Integer getDocId()
	{
		return docId;
	}

	public void setDocId(Integer docId)
	{
		this.docId = docId;
	}

	public String getDocName()
	{
		return docName;
	}

	public void setDocName(String docName)
	{
		this.docName = docName;
	}

	public String getDiagn()
	{
		return diagn;
	}

	public void setDiagn(String diagn)
	{
		this.diagn = diagn;
	}
}
