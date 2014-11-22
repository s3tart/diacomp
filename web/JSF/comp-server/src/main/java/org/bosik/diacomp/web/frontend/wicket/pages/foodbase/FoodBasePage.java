package org.bosik.diacomp.web.frontend.wicket.pages.foodbase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.bosik.diacomp.core.entities.business.foodbase.FoodItem;
import org.bosik.diacomp.core.entities.tech.Versioned;
import org.bosik.diacomp.core.services.foodbase.FoodBaseService;
import org.bosik.diacomp.web.backend.features.foodbase.service.FrontendFoodbaseService;
import org.bosik.diacomp.web.frontend.wicket.dialogs.FoodEditor;
import org.bosik.diacomp.web.frontend.wicket.pages.master.MasterPage;

public class FoodBasePage extends MasterPage
{
	private static final long				serialVersionUID	= 1L;

	WebMarkupContainer						container;

	transient static final FoodBaseService	foodService			= new FrontendFoodbaseService();
	String									search;

	public FoodBasePage(PageParameters parameters)
	{
		super(parameters);

		TextField<String> textSearch = new TextField<String>("inputSearchName", new PropertyModel<String>(this,
				"search"));
		textSearch.add(new AjaxFormComponentUpdatingBehavior("onkeyup")
		{
			private static final long	serialVersionUID	= 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				target.add(container);
			}
		});
		add(textSearch);

		final FoodEditor foodEditor = new FoodEditor("foodEditor")
		{
			private static final long	serialVersionUID	= 1L;

			@Override
			public void onSelect(AjaxRequestTarget target, Model<Versioned<FoodItem>> model)
			{
				System.out.println("Saving: " + model.getObject().getData().getName());

				model.getObject().updateTimeStamp();
				foodService.save(Arrays.asList(model.getObject()));

				target.add(container);
				close(target);
			}

			@Override
			public void onCancel(AjaxRequestTarget target)
			{
				close(target);
			}
		};
		add(foodEditor);

		container = new WebMarkupContainer("tableContainer");
		container.setOutputMarkupId(true);
		add(container);

		container.add(new RefreshingView<Versioned<FoodItem>>("view")
		{
			private static final long	serialVersionUID	= 1L;

			@Override
			protected Iterator<IModel<Versioned<FoodItem>>> getItemModels()
			{
				final List<IModel<Versioned<FoodItem>>> list = new ArrayList<IModel<Versioned<FoodItem>>>();

				//				FoodDataProvider provider = new FoodDataProvider();
				//				Iterator<? extends Versioned<FoodItem>> it = provider.iterator(0, 19);
				//				while (it.hasNext())
				//				{
				//					foodBase.add(provider.model(it.next()));
				//				}

				if (search == null)
				{
					search = "";
				}

				List<Versioned<FoodItem>> foods = foodService.findAny(search);
				if (foods.size() > 20)
				{
					foods = foods.subList(0, 20);
				}
				for (Versioned<FoodItem> food : foods)
				{
					list.add(Model.of(food));
				}

				return list.iterator();
			}

			@Override
			protected void populateItem(final Item<Versioned<FoodItem>> item)
			{
				FoodItem food = item.getModelObject().getData();

				item.add(new Label("food.name", food.getName()));
				item.add(new Label("food.prots", food.getRelProts()));
				item.add(new Label("food.fats", food.getRelFats()));
				item.add(new Label("food.carbs", food.getRelCarbs()));
				item.add(new Label("food.value", food.getRelValue()));

				item.add(new AjaxEventBehavior("onclick")
				{
					private static final long	serialVersionUID	= 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target)
					{
						Versioned<FoodItem> food = item.getModelObject();
						System.out.println("Opening: " + food.getData().getName());

						foodEditor.show(target, Model.of(food));
					}
				});
			}
		});
	}
}
