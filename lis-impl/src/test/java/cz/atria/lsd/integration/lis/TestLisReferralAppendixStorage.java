package cz.atria.lsd.integration.lis;

import cz.atria.common.spring.jdbc.CustomJdbcTemplate;
import cz.atria.lsd.integration.md.lis.api.LisReferralAppendixStorage;
import cz.atria.md.referral.ReferralAppendixStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import java.io.InputStream;

/**
 * User: tnurtdinov
 * Date: 07.12.12
 * Time: 18:17
 */
//@RunWith (SpringJUnit4ClassRunner.class)
@ContextConfiguration ("classpath:cz/atria/lsd/integration/lis/lis-impl-test-context.xml")
public class TestLisReferralAppendixStorage
{
	@Autowired
	private ReferralAppendixStorage referralAppendixStorage;

	@Autowired
	@Qualifier ("lisJdbcTemplate")
	private CustomJdbcTemplate jdbcTemplate;

	@Autowired
	@Qualifier("lisReferralAppendixStorage")
	private LisReferralAppendixStorage lisReferralAppendixStorage;

	//@Test
	public void testDownload() throws Exception
	{
		lisReferralAppendixStorage.init();
		//for ()
		InputStream io = lisReferralAppendixStorage.getContent("/home/lisftp/test.pdf");
		referralAppendixStorage.saveContent(io, "ftpFile2.pdf");
		lisReferralAppendixStorage.destroy();
	}
}
