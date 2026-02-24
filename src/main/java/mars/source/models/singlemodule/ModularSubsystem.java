package mars.source.models.singlemodule;

import java.util.function.Supplier;

import com.stzteam.forgemini.io.IOSubsystem;

import edu.wpi.first.wpilibj2.command.Command;
import mars.source.diagnostics.ActionStatus;
import mars.source.models.SubsystemBuilder;
import mars.source.models.Telemetry;
import mars.source.requests.Request;

public abstract class ModularSubsystem<D extends Data<D>, A extends IO<D>> extends IOSubsystem {

    protected final D inputs;
    protected final A actor;
    private Request<D, A> currentRequest;
    private Telemetry<D> telemetry;
    private ActionStatus lastStatus;
    public final boolean isFallback;

    protected ModularSubsystem(SubsystemBuilder<D, A> builder) {
        super(builder.getKey());
        this.inputs = builder.getInputs();
        this.actor = builder.getActor();
        this.currentRequest = builder.getInitialRequest();
        this.telemetry = builder.getTelemetry();
        this.lastStatus = ActionStatus.ok();

        this.isFallback = actor.isFallback();
    }

    @Override
    public final void periodicLogic(){

        if(actor.isFallback()) return;

        actor.updateInputs(inputs);

        D data = inputs.snapshot();

        absolutePeriodic(data);

        if (currentRequest != null) {
            this.lastStatus = currentRequest.apply(data, actor);
        }

        if (telemetry != null) {
            telemetry.telemeterize(data, lastStatus);
        }

    }

    public abstract D getState();


    public abstract void absolutePeriodic(D inputs);

    public void registerTelemetry(Telemetry<D> telemetry) {
        this.telemetry = telemetry;
    }

    public void setRequest(Request<D, A> newRequest) {
        if (newRequest != null) {
            this.currentRequest = newRequest;
        }
    }

    public Command runRequest(Supplier<? extends Request<D, A>> requestSupplier) {
        return this.run(() -> this.setRequest(requestSupplier.get()));
    }

    public Command runRequestUntilDone(Supplier<? extends Request<D, A>> requestSupplier) {
        return this.run(() -> this.setRequest(requestSupplier.get()))
                   .until(() -> {
                       ActionStatus status = this.getLastStatus();
                       return status != null && status.isDone();
                   });
    }

    protected void overrideStatus(ActionStatus emergencyStatus) {
        this.lastStatus = emergencyStatus;
    }

    public ActionStatus getLastStatus() {
        return lastStatus;
    }
}