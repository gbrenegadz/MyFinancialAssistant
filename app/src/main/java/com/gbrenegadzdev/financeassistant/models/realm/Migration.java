package com.gbrenegadzdev.financeassistant.models.realm;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class Migration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 0) {
            RealmObjectSchema testSchema = schema.get(Test.class.getSimpleName());
            if (testSchema != null) {
                testSchema.addField(Test.TEST_ID, String.class);
                testSchema.addField(Test.TEST_NAME, String.class);
            }

            RealmObjectSchema budgetSchema = schema.get(Budget.class.getSimpleName());
            if (budgetSchema != null) {
                budgetSchema.addField(Budget.BUDGET_ID, String.class);
                budgetSchema.addField(Budget.BUDGET_NAME, String.class);
                budgetSchema.addField(Budget.AMOUNT, double.class);
                budgetSchema.addField(Budget.CATEGORY, String.class);
                budgetSchema.addField(Budget.MONTH, String.class);
                budgetSchema.addField(Budget.YEAR, int.class);
                budgetSchema.addField(Budget.CREATED_DATETIME, Date.class);
                budgetSchema.addField(Budget.MODIFIED_DATETIME, Date.class);
            }

            RealmObjectSchema salaryDeductionSchema = schema.get(SalaryDeductionSetup.class.getSimpleName());
            if (salaryDeductionSchema != null) {
                salaryDeductionSchema.addField(SalaryDeductionSetup.SALARY_DEDUCTION_ID, String.class);
                salaryDeductionSchema.addField(SalaryDeductionSetup.DEDUCTION_NAME, String.class);
                salaryDeductionSchema.addField(SalaryDeductionSetup.CREATED_DATETIME, Date.class);
                salaryDeductionSchema.addField(SalaryDeductionSetup.MODIFIED_DATETIME, Date.class);
                salaryDeductionSchema.addField(SalaryDeductionSetup.IS_SELECTED, boolean.class);
            }
        }
    }
}
