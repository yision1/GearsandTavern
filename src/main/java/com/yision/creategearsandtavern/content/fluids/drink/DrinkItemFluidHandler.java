package com.yision.creategearsandtavern.content.fluids.drink;

import com.yision.creategearsandtavern.registry.CGTFluids;

import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public final class DrinkItemFluidHandler implements IFluidHandlerItem {
	private ItemStack container;

	public DrinkItemFluidHandler(ItemStack container) {
		this.container = container;
	}

	@Override
	public ItemStack getContainer() {
		return container;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return tank == 0 ? DrinkFluidConversions.fromItem(container) : FluidStack.EMPTY;
	}

	@Override
	public int getTankCapacity(int tank) {
		return tank == 0 ? CGTDrinkCatalog.SERVING_AMOUNT : 0;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		if (tank != 0 || stack.isEmpty() || container.isEmpty()
			|| CGTDrinkCatalog.byItem(container.getItem()) != null || !KaleidoscopeDrinkFluid.hasValidIdentity(stack)) {
			return false;
		}
		return CGTFluids.definition(stack.getFluid()).containerItem() == container.getItem();
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (resource.getAmount() < CGTDrinkCatalog.SERVING_AMOUNT || !isFluidValid(0, resource)
			|| action.execute() && container.getCount() != 1) {
			return 0;
		}
		ItemStack filled = DrinkFluidConversions.toItem(resource);
		if (filled.isEmpty()) {
			return 0;
		}
		if (!container.getComponentsPatch().isEmpty()) {
			ItemStack decorated = filled.copy();
			decorated.applyComponents(container.getComponentsPatch());
			if (!ItemStack.isSameItemSameComponents(decorated, filled)) {
				return 0;
			}
		}
		if (action.execute()) {
			container = filled;
		}
		return CGTDrinkCatalog.SERVING_AMOUNT;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, getFluidInTank(0))) {
			return FluidStack.EMPTY;
		}
		return drain(resource.getAmount(), action);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		if (maxDrain < CGTDrinkCatalog.SERVING_AMOUNT || container.isEmpty()
			|| action.execute() && container.getCount() != 1) {
			return FluidStack.EMPTY;
		}
		FluidStack held = getFluidInTank(0);
		if (held.isEmpty()) {
			return FluidStack.EMPTY;
		}
		if (action.execute()) {
			container = CGTDrinkCatalog.byItem(container.getItem()).containerItem().getDefaultInstance();
		}
		return held;
	}
}
