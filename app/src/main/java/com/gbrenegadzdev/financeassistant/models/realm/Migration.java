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

            RealmObjectSchema salaryDeductionSetupSchema = schema.get(SalaryDeductionSetup.class.getSimpleName());
            if (salaryDeductionSetupSchema != null) {
                salaryDeductionSetupSchema.addField(SalaryDeductionSetup.SALARY_DEDUCTION_ID, String.class);
                salaryDeductionSetupSchema.addField(SalaryDeductionSetup.DEDUCTION_NAME, String.class);
                salaryDeductionSetupSchema.addField(SalaryDeductionSetup.CREATED_DATETIME, Date.class);
                salaryDeductionSetupSchema.addField(SalaryDeductionSetup.MODIFIED_DATETIME, Date.class);
                salaryDeductionSetupSchema.addField(SalaryDeductionSetup.IS_SELECTED, boolean.class);
            }

            RealmObjectSchema categorySetupSchema = schema.get(CategorySetup.class.getSimpleName());
            if (categorySetupSchema != null) {
                categorySetupSchema.addField(CategorySetup.CATEGORY_ID, String.class);
                categorySetupSchema.addField(CategorySetup.CATEGORY_NAME, String.class);
                categorySetupSchema.addField(CategorySetup.CREATED_DATETIME, Date.class);
                categorySetupSchema.addField(CategorySetup.IS_EDITABLE, boolean.class);
                categorySetupSchema.addField(CategorySetup.IS_DELETABLE, boolean.class);
            }

            RealmObjectSchema subCategorySetupSchema = schema.get(SubCategorySetup.class.getSimpleName());
            if (subCategorySetupSchema != null) {
                subCategorySetupSchema.addField(SubCategorySetup.SUB_CATEGORY_ID, String.class);
                subCategorySetupSchema.addField(SubCategorySetup.SUB_CATEGORY_NAME, String.class);
                subCategorySetupSchema.addField(SubCategorySetup.CREATED_DATETIME, Date.class);
                subCategorySetupSchema.addField(SubCategorySetup.IS_EDITABLE, boolean.class);
                subCategorySetupSchema.addField(SubCategorySetup.IS_DELETABLE, boolean.class);
                subCategorySetupSchema.addField(SubCategorySetup.IS_SHOWN, boolean.class);
            }

            RealmObjectSchema incomeSchema = schema.get(Income.class.getSimpleName());
            if (incomeSchema != null) {
                incomeSchema.addField(Income.INCOME_ID, String.class);
                incomeSchema.addField(Income.INCOME_NAME, String.class);
                incomeSchema.addField(Income.AMOUNT, double.class);
                incomeSchema.addField(Income.MONTH, String.class);
                incomeSchema.addField(Income.YEAR, int.class);
                incomeSchema.addField(Income.CREATED_DATETIME, Date.class);
                incomeSchema.addField(Income.MODIFIED_DATETIME, Date.class);
            }

            RealmObjectSchema expenseSchema = schema.get(Expense.class.getSimpleName());
            if (expenseSchema != null) {
                expenseSchema.addField(Expense.EXPENSE_ID, String.class);
                expenseSchema.addField(Expense.EXPENSE_NAME, String.class);
                expenseSchema.addField(Expense.AMOUNT, double.class);
                expenseSchema.addField(Expense.PAID_TO, String.class);
                expenseSchema.addField(Expense.CATEGORY, String.class);
                expenseSchema.addField(Expense.MONTH, String.class);
                expenseSchema.addField(Expense.YEAR, int.class);
                expenseSchema.addField(Expense.CREATED_DATETIME, Date.class);
                expenseSchema.addField(Expense.MODIFIED_DATETIME, Date.class);
            }
        }
    }
}
