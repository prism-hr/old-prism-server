package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.builders.RejectReasonBuilder;

public class RejectReasonDAOTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldReturnRejectReasonsInIDOrder() {
		BigInteger numberOfReasons = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from REJECT_REASON").uniqueResult();
		int reasonCount = numberOfReasons.intValue();
		RejectReason reason1 = new RejectReasonBuilder().text("reason1").build();
		RejectReason reason2 = new RejectReasonBuilder().text("reason2").build();
		save(reason1, reason2);
		flushAndClearSession();

		RejectReasonDAO rejectDAO = new RejectReasonDAO(sessionFactory);
		List<RejectReason> allReasons = rejectDAO.getAllReasons();

		Assert.assertEquals(reasonCount + 2, allReasons.size());
		assertEquals("reason1", allReasons.get(reasonCount).getText());
		assertEquals("reason2", allReasons.get(reasonCount + 1).getText());
	}
	
	@Test
	public void shouldGetRejectReasonById(){
		RejectReason reason = new RejectReasonBuilder().text("reason1").build();		
		save(reason);
		flushAndClearSession();

		RejectReasonDAO rejectDAO = new RejectReasonDAO(sessionFactory);
		assertEquals(reason.getId(), rejectDAO.getRejectReasonById(reason.getId()).getId());
	}
}




