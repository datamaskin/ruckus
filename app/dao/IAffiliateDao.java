package dao;

import models.business.AffiliateCode;
import models.business.AffiliatePlan;

import java.util.List;

/** The interface for Dao implementations for affiliates. */
public interface IAffiliateDao {
    /** Find all {@link models.business.AffiliatePlan} object with the given id. */
    public AffiliatePlan affiliatePlanForId(final Long affiliatePlanId);

    /** Find all {@link models.business.AffiliatePlan} objects. */
    public List<AffiliatePlan> allAffiliatePlans();

    /** Find all {@link models.business.AffiliatePlan} object with the given id. */
    public AffiliateCode affiliateCodeForId(final Long affiliateCodeId);

    /** Find all {@link models.business.AffiliateCode} objects. */
    public List<AffiliateCode> allAffiliateCodes();

    /** Find all {@link models.business.AffiliateCode} objects for a particular user. */
    public List<AffiliateCode> affiliateCodesForUser(final Long userId);

    /** Finds the affiliate code for the given string UUID which will be passed in a URL. */
    public AffiliateCode affiliateCodeForUUID(final String uuid);
}
