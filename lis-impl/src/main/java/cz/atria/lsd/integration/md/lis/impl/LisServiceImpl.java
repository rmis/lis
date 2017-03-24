package cz.atria.lsd.integration.md.lis.impl;

import cz.atria.common.spring.jdbc.CustomJdbcTemplate;
import cz.atria.lsd.integration.md.lis.api.IntegrationLisReferral;
import cz.atria.lsd.integration.md.lis.api.IntegrationLisReferralService;
import cz.atria.lsd.integration.md.lis.api.LisReferralAppendixStorage;
import cz.atria.lsd.integration.md.lis.api.LisService;
import cz.atria.md.referral.Referral;
import cz.atria.md.referral.ReferralAppendix;
import cz.atria.md.referral.ReferralAppendixService;
import cz.atria.md.referral.ReferralToService;
import cz.atria.ns.service.management.entity.Service;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import java.io.InputStream;
import java.util.List;


public class LisServiceImpl implements LisService
{
	@Autowired
	@Qualifier ("lisJdbcTemplate")
	private CustomJdbcTemplate jdbcTemplate;

	@Autowired
	private ReferralAppendixService referralAppendixService;

	@Autowired
	private IntegrationLisReferralService integrationLisReferralService;

	@Autowired
	private LisReferralAppendixStorage lisReferralAppendixStorage;

	private static final String INSERT_SAMPLE = "insert into sample(tab_key, refid, lab_id, pid, card_num, fam, name, otch, birthday, sex, doc_id, doc_name, diagn, cito) " +
			"values (nextval('sample_seq'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String SELECT_REFERRAL_IDS = "select refid from sample";
	private static final String INSERT_ORDER = "insert into orders ( tab_key, sample_id, orderid) values (nextval('orders_seq'), ?, ?)";
	private static final String SELECT_RESULT_PATH = "select path from result where refid = ?";
	private static final String SELECT_ID_FROM_SAMPLE_BY_REFID = "select tab_key from sample where refid = ?";
	private final org.slf4j.Logger log = LoggerFactory.getLogger(LisServiceImpl.class.getName());

	@Override
	@Transactional
	public void sendReferrals()
	{
		log.info("starting to send referrals to LIS");
		//List<Integer> referralIds = jdbcTemplate.queryForList(SELECT_REFERRAL_IDS, Integer.class);
		firstStageOfSending();
		secondStageOfSending();
	}

	@Override
	@Transactional
	public void updateReferrals()
	{
		log.info("starting to attach files for referrals");
		List<IntegrationLisReferral> integrationLisReferralList = integrationLisReferralService
				.getNotAttachedIntegrationLisReferralList();
		lisReferralAppendixStorage.init();
		for (IntegrationLisReferral integrationLisReferral : integrationLisReferralList)
		{
			try
			{
				String path = selectResultPath(integrationLisReferral.getReferralId());
				//String path = "/home/iadmin/file.pdf";
				if (path != null)
				{
					Integer appendixId = saveReferralAppendix(path, integrationLisReferral.getReferralId());
					if (appendixId != null)
					{
						integrationLisReferral.setAttached(true);
						integrationLisReferralService.saveIntegrationLisReferral(integrationLisReferral);
					}
				}
			}
			catch (Exception e)
			{
				log.error("could not attach appendix for referral with id = " + integrationLisReferral.getReferralId(), e);
			}

		}
		lisReferralAppendixStorage.destroy();
	}

	private void firstStageOfSending()
	{
		log.info("first stage of sending");
		List<Referral> referrals = integrationLisReferralService.getReferralsOnSend();
		for (Referral referral : referrals)
		{
			try
			{
				sendNewReferral(referral);
			}
			catch (Exception e)
			{
				log.error("referral with id = " + referral.getId() + " could not send", e);
			}
		}
	}

	private void secondStageOfSending()
	{
		log.info("second stage of sending");
		List<Referral> referrals = integrationLisReferralService.getNotSentReferrals();
		for (Referral referral : referrals)
		{
			try
			{
				log.error("sendNotSentReferral");
				sendNotSentReferral(referral);
			}
			catch (Exception e)
			{
				log.error("second stage of sending... referral with id = " + referral.getId() + " could not send", e);
			}
		}
	}

	private void sendNewReferral(Referral referral)
	{
		IntegrationLisReferral integrationLisReferral = integrationLisReferralService
							.saveIntegrationLisReferral(new IntegrationLisReferral(referral.getId()));
		Integer sampleId = insertSample(referral);
		if (sampleId != null)
		{
			integrationLisReferral.setSampleId(sampleId);
			integrationLisReferralService.saveIntegrationLisReferral(integrationLisReferral);
		}
		if (sampleId != null && referral.getServices() != null)
		{
			for (ReferralToService referralToService : referral.getServices())
			{
				if (referralToService.getService() != null)
				{
					try
					{
						insertOrder(sampleId, referralToService.getService());
					}
					catch (Exception e)
					{
						log.error("referralToService with id = " +referralToService.getId() + " could not send", e);
					}
				}
			}
		}
	}


	private void sendNotSentReferral(Referral referral)
	{
		Integer sampleId = selectSampleId(referral.getId());
		if (sampleId == null)
		{
			sendNewReferral(referral);
		}
		else
		{
			log.info("referral with id = " + referral.getId() + " was sent, updating integration table...");
		  IntegrationLisReferral integrationLisReferral = integrationLisReferralService
					.getIntegrationLisReferral(referral.getId());
			integrationLisReferral.setSampleId(sampleId);
			integrationLisReferralService.saveIntegrationLisReferral(integrationLisReferral);
			log.info(" was updated");
		}
	}


	/*private PreparedStatementCreator getPreparedStatementCreator(Referral referral)
	{
		final ReferralModel referralModel = new ReferralModel(referral);
		return new PreparedStatementCreator()
		{

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException
			{
				PreparedStatement ps =
						con.prepareStatement(INSERT_SAMPLE, new String[]{"id"});
				ps.setInt(1, referralModel.getRefId());
				// lab_id
				ps.setInt(2, referralModel.getLabId());
				//pid
				ps.setString(3, referralModel.getPid());
				//card_num
				ps.setString(4, referralModel.getCurdNum());
				//fam
				ps.setString(5, referralModel.getFam());
				//name
				ps.setString(6, referralModel.getName());
				//otch
				ps.setString(7, referralModel.getOtch());
				//birthday
				ps.setDate(8, (referralModel.getBirthDay() != null) ? new Date(referralModel.getBirthDay().getTime()) : null);
				//sex
				ps.setInt(9, referralModel.getSex());
				//doc_id
				if (referralModel.getDocId()!=null)
				{
					ps.setInt(10, referralModel.getDocId());
				}
				else
				{
					ps.setNull(10,);
				}
				//doc_name
				ps.setString(11, referralModel.getDocName());
				//diagn
				ps.setString(12, referralModel.getDiagn());


				return ps;
			}
		};


	}
*/
	private Integer insertSample(Referral referral)
	{
		return jdbcTemplate.updateReturningKeyInteger(INSERT_SAMPLE, "tab_key", new Object[]{

				//refid
				referral.getId(),
				// lab_id
				(referral.getReceivingOrganization() != null) ? referral.getReceivingOrganization()
						.getId() : null,
				//pid
				referral.getPatient().getIndividual().getUid(),
				//card_num
				referral.getPatient().getIndividual().getUid(),
				//fam
				referral.getPatient().getIndividual().getSurname(),
				//name
				referral.getPatient().getIndividual().getName(),
				//otch
				referral.getPatient().getIndividual().getPatrName(),
				//birthday
				referral.getPatient().getIndividual().getBirthDate(),
				//sex
				(referral.getPatient().getIndividual().getGender() != null) ? referral.getPatient().getIndividual()
						.getGender().getId() : null,
				//doc_id
				(referral.getDoctor() != null) ? referral.getDoctor().getEmployeePosition().getEmployee()
						.getId() : null,
				//doc_name
				(referral.getDoctor() != null) ? referral.getDoctor().getEmployeePosition().getEmployee()
						.getIndividual().getFullName() : null,
				//diagn
				(referral.getDiagnosis() != null) ? referral.getDiagnosis().getCode() : null,
				//cito - признак срочности
				referral.getUrgent()
		});
	}

	private void insertOrder(Integer sampleId, Service service)
	{
		jdbcTemplate.updateReturningKeyInteger(INSERT_ORDER, "tab_key", new Object[]{
				//sample_id
				sampleId,
				//order_id
				service.getCode()
		});
	}

	private String selectResultPath(Integer referralId)
	{
		List<String> result = jdbcTemplate.queryForList(SELECT_RESULT_PATH, new Object[]{referralId}, String.class);
		return (result.isEmpty()) ? null : result.get(0);

	}

	private Integer selectSampleId(Integer referralId)
	{
		List<Integer> result = jdbcTemplate
				.queryForList(SELECT_ID_FROM_SAMPLE_BY_REFID, new Object[]{referralId}, Integer.class);
		return (result.isEmpty()) ? null : result.get(0);
	}

	private boolean isSent(Referral referral, List<Integer> sentReferralIds)
	{
		for (Integer id : sentReferralIds)
		{
			if (referral.getId().equals(id))
			{
				return true;
			}
		}
		return false;
	}

	public Integer saveReferralAppendix(String path, Integer referralId)
	{
		/*FileInputStream fis = null;
		try {
			fis = new FileInputStream("file.pdf");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/
		InputStream io = lisReferralAppendixStorage.getContent(path);
		//InputStream io = fis;
		ReferralAppendix referralAppendix = new ReferralAppendix();
		referralAppendix.setAppendixName(getFileName(path));
        referralAppendix.setFileName(referralAppendix.getAppendixName());
		referralAppendix.setReferral(new Referral(referralId));
		referralAppendixService.addReferralAppendix(referralAppendix, io);
		return referralAppendix.getId();
	}

	private String getFileName(String path)
	{
		int i = path.lastIndexOf('/');
		if (i == -1)
			return path;

		else
		{
			if (!(i + 1 > path.length()))
				return path.substring(i + 1);
		}
		return null;
		//return "file.pdf";
	}
}
