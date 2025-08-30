package ru.expensive.api.feature.module;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.expensive.implement.features.modules.combat.*;
import ru.expensive.implement.features.modules.misc.*;
import ru.expensive.implement.features.modules.movement.*;
import ru.expensive.implement.features.modules.player.*;
import ru.expensive.implement.features.modules.render.*;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModuleRepository {
    List<Module> modules = new ArrayList<>();

    public void setup() {
        register(
                new InterfaceModule(),
                new AuctionHelperModule(),
                new PearlPredictionModule(),
                new AuraModule(),
                new AntiBot(),
                //new FlightModule(),
                new NoFriendDamageModule(),
                new HitBoxModule(),
                new BindSwapModule(),
                new AutoSprintModule(),
                new NoPushModule(),
                new ClickPearlModule(),
                new NoDelayModule(),
                new AutoRespawnModule(),
                new ScreenWalkModule(),
                new ServerRPSpooferModule(),
                new TriggerBotModule(),
                new AutoLeaveModule(),
                new AspectRatioModule(),
                new AutoGappleModule(),
                new AntiAFKModule(),
                new FishingModule(),
                new AutoEatModule(),
                new FunTimeBindsModule(),
                new EffectCancelModule(),
                new ClearRenderModule(),
                new NightVisionModule(),
                new QuickStopModule(),
                new AutoTotemModule(),
                new PluginsModule(),
                new ElytraHelperModule(),
                new TwerkModule(),
                new SpeedModule(),
                new ColorHudModule(),
                new TracersModule(),
                new TargetESPModule()
                , new JumpCircleModule()
                , new HitParticlesModule()
                , new ESPModule()
        );
    }

    public void register(Module... module) {
        modules.addAll(List.of(module));
    }

    public List<Module> modules() {
        return modules;
    }
}
