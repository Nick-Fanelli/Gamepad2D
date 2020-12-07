package gamepad.physics2D.forces;

import gamepad.physics2D.rigidbody.Rigidbody2D;

import java.util.ArrayList;

public class ForceRegistry {

    private ArrayList<ForceRegistration> registry;

    public ForceRegistry() {
        this.registry = new ArrayList<>();
    }

    public void add(Rigidbody2D rb, ForceGenerator fg) {
        ForceRegistration fr = new ForceRegistration(fg, rb);
        registry.add(fr);
    }

    public void remove(Rigidbody2D rb, ForceGenerator fg) {
        ForceRegistration fr = new ForceRegistration(fg, rb);
        registry.remove(fr);
    }

    public void clear() { this.registry.clear(); }

    public void updateForces(float deltaTime) {
        for(ForceRegistration fr : this.registry) {
            fr.fg.updateForce(fr.rb, deltaTime);
        }
    }

    public void zeroForces() {
        for(ForceRegistration fr : registry) {
            // TODO: IMPLEMENT
//            fr.rb.zeroForces();
        }
    }

}
